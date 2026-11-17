import java.io.*;
import java.util.*;

public class SistemEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private String namaEvent;
    private HashMap<String, Peserta> daftarPeserta = new HashMap<>();
    private ArrayList<TiketAcara> daftarTiket = new ArrayList<>();
    private ArrayList<Transaksi> riwayat = new ArrayList<>();

    public SistemEvent(String namaEvent) { this.namaEvent = namaEvent; }

    public void tambahPeserta(String id, String nama) {
        daftarPeserta.put(id, new Peserta(id, nama));
    }

    public void tambahTiketAcara(String kode, String nama, int kuota) {
        String n = nama.toLowerCase();
        TiketAcara t;
        if (n.contains("vip") || n.contains("premium")) t = new TiketVIP(kode, nama, kuota);
        else if (n.contains("reguler")) t = new TiketReguler(kode, nama, kuota);
        else t = new TiketEkonomis(kode, nama, kuota);
        daftarTiket.add(t);
    }

    private TiketAcara cariTiket(String kode) throws TiketTidakDitemukanException {
        for (TiketAcara t : daftarTiket) {
            if (t.getKode().equals(kode)) return t;
        }
        throw new TiketTidakDitemukanException("Tiket " + kode + " tidak ditemukan");
    }

    public void jalankanTransaksiDariFile(String namaFile) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(namaFile))) {
            String baris;
            while ((baris = br.readLine()) != null) sb.append(baris).append(' ');
        }
        String isi = sb.toString().trim();
        if (isi.isEmpty()) throw new IOException("File kosong");

        String[] records = isi.split("\\s+");
        int no = 0;
        for (String rec : records) {
            no++;
            try {
                Transaksi t = parseRecord(rec);
                prosesTransaksi(t);
            } catch (FormatDataException | PesertaTidakDitemukanException
                     | TiketTidakDitemukanException | KuotaTidakCukupException e) {
                System.out.println("[TRANSAKSI " + no + " GAGAL] " + e.getMessage());
            }
        }
    }

    private Transaksi parseRecord(String rec) throws FormatDataException {
        try {
            String bersih = rec.endsWith(",") ? rec.substring(0, rec.length() - 1) : rec;
            String id = null, kd = null;
            int jml = -1;
            for (String f : bersih.split(",")) {
                String[] kv = f.split("=");
                if (kv.length != 2) throw new FormatDataException("Format field salah: '" + f + "'");
                switch (kv[0].trim()) {
                    case "ID":  id = kv[1].trim(); break;
                    case "KD":  kd = kv[1].trim(); break;
                    case "JML": jml = Integer.parseInt(kv[1].trim()); break;
                    default: throw new FormatDataException("Field tidak dikenal: " + kv[0]);
                }
            }
            if (id == null || kd == null || jml <= 0)
                throw new FormatDataException("Data tidak lengkap/JML tidak valid: " + rec);
            return new Transaksi(id, kd, jml);
        } catch (NumberFormatException e) {
            throw new FormatDataException("JML bukan angka pada: " + rec);
        }
    }

    private void prosesTransaksi(Transaksi t) throws PesertaTidakDitemukanException,
            TiketTidakDitemukanException, KuotaTidakCukupException {
        riwayat.add(t);
        try {
            Peserta p = daftarPeserta.get(t.getIdPeserta());
            if (p == null)
                throw new PesertaTidakDitemukanException("Peserta " + t.getIdPeserta() + " tidak terdaftar");
            t.setPeserta(p);

            TiketAcara tk = cariTiket(t.getKodeTiket());
            t.setTiket(tk);

            tk.kurangiKuota(t.getQty());
            p.tambahTiketDibeli(t.getQty());
            t.setTotalHarga(tk.hitungTotal(t.getQty()));
            t.setStatus("BERHASIL");
            System.out.println("[OK] " + p.getNama() + " beli " + t.getQty() + " x " + tk.getNama()
                    + " (" + tk.getKategori() + ") = Rp" + t.getTotalHarga());
        } catch (PesertaTidakDitemukanException | TiketTidakDitemukanException | KuotaTidakCukupException e) {
            t.setStatus("GAGAL");
            throw e;
        }
    }

    public void simpanLaporanAkhir() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("state.bak"))) {
            oos.writeObject(this);
            System.out.println("[SERIALIZE] Status sistem disimpan ke state.bak");
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal serialisasi: " + e.getMessage());
        }

        try (FileWriter fw = new FileWriter("laporan.txt")) {
            String garis = "-------------------------------------------------------------------------------------------\n";
            fw.write("LAPORAN " + namaEvent.toUpperCase() + "\n\n");

            fw.write("RIWAYAT TRANSAKSI\n" + garis);
            fw.write(String.format("%-4s %-9s %-8s %-8s %-5s %-12s %-8s%n",
                    "No", "ID Aktor", "Nama", "Kode", "Qty", "Total (Rp)", "Status"));
            fw.write(garis);
            int no = 1;
            for (Transaksi t : riwayat) {
                String nama = (t.getPeserta() != null) ? t.getPeserta().getNama() : "-";
                fw.write(String.format("%-4d %-9s %-8s %-8s %-5d %-12d %-8s%n",
                        no++, t.getIdPeserta(), nama, t.getKodeTiket(),
                        t.getQty(), t.getTotalHarga(), t.getStatus()));
            }
            fw.write(garis + "\n");

            fw.write("SISA KUOTA TIKET\n" + garis);
            fw.write(String.format("%-8s %-22s %-10s %-12s %-6s%n",
                    "Kode", "Nama Acara", "Kategori", "Harga (Rp)", "Sisa"));
            fw.write(garis);
            for (TiketAcara tk : daftarTiket) {
                fw.write(String.format("%-8s %-22s %-10s %-12d %-6d%n",
                        tk.getKode(), tk.getNama(), tk.getKategori(), tk.getHargaSatuan(), tk.getKuota()));
            }
            fw.write(garis);
            System.out.println("[LAPORAN] laporan.txt berhasil dibuat");
        } catch (IOException e) {
            System.out.println("[ERROR] Gagal menulis laporan: " + e.getMessage());
        }
    }
}