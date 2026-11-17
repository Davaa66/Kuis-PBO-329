public class TiketReguler extends TiketAcara {
    private static final long serialVersionUID = 1L;
    public TiketReguler(String k, String n, int q) { super(k, n, q); }
    @Override public long getHargaSatuan() { return 100000; }
    @Override public String getKategori() { return "Reguler"; }
}