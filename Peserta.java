public class Peserta extends Aktor {
    private static final long serialVersionUID = 1L;
    private int totalTiketDibeli;

    public Peserta(String id, String nama) { super(id, nama); }

    public void tambahTiketDibeli(int qty) { this.totalTiketDibeli += qty; }
    public int getTotalTiketDibeli() { return totalTiketDibeli; }

    @Override
    public String getPeran() { return "Peserta"; }
}
