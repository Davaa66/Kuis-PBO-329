import java.io.Serializable;

public class Transaksi implements Serializable {
    private static final long serialVersionUID = 1L;
    private String idPeserta;
    private String kodeTiket;
    private int qty;
    private Peserta peserta;   
    private TiketAcara tiket; 
    private long totalHarga;
    private String status = "PENDING";

    public Transaksi(String idPeserta, String kodeTiket, int qty) {
        this.idPeserta = idPeserta;
        this.kodeTiket = kodeTiket;
        this.qty = qty;
    }

    public String getIdPeserta() { return idPeserta; }
    public String getKodeTiket() { return kodeTiket; }
    public int getQty() { return qty; }
    public Peserta getPeserta() { return peserta; }
    public TiketAcara getTiket() { return tiket; }
    public long getTotalHarga() { return totalHarga; }
    public String getStatus() { return status; }

    public void setPeserta(Peserta p) { this.peserta = p; }
    public void setTiket(TiketAcara t) { this.tiket = t; }
    public void setTotalHarga(long t) { this.totalHarga = t; }
    public void setStatus(String s) { this.status = s; }
}