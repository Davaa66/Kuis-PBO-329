public class TiketVIP extends TiketAcara {
    private static final long serialVersionUID = 1L;
    public TiketVIP(String k, String n, int q) { super(k, n, q); }
    @Override public long getHargaSatuan() { return 500000; }
    @Override public String getKategori() { return "VIP"; }
}