public class TiketEkonomis extends TiketAcara {
    private static final long serialVersionUID = 1L;
    public TiketEkonomis(String k, String n, int q) { super(k, n, q); }
    @Override public long getHargaSatuan() { return 50000; }
    @Override public String getKategori() { return "Ekonomis"; }
}