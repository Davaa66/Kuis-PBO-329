import java.io.Serializable;
public abstract class TiketAcara implements Serializable {
    private static final long serialVersionUID = 1L;
    private String kode;
    private String nama;
    private int kuota;

    public TiketAcara(String kode, String nama, int kuota) {
        this.kode = kode;
        this.nama = nama;
        this.kuota = kuota;
    }

    public String getKode() { return kode; }
    public String getNama() { return nama; }
    public int getKuota() { return kuota; }

    public void kurangiKuota(int qty) throws KuotaTidakCukupException {
        if (qty > kuota) {
            throw new KuotaTidakCukupException("Kuota " + kode + " tidak cukup (sisa " + kuota + ", diminta " + qty + ")");
        }
        kuota -= qty;
    }

    public abstract long getHargaSatuan();
    public abstract String getKategori();

    public long hitungTotal(int qty) { return getHargaSatuan() * qty; }
}