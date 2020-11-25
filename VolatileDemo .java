import java.util.concurrent.locks.ReentrantLock;


class Tip {
    private String key;
    private String val;

    public void put(String key, String val) {
        this.key = key;
        this.val = val;
    }

    public String getKey() {
        return key;
    }

    public String getVal() {
        return val;
    }
}

public class VolatileDemo {

    private volatile static Tip tip = new Tip();
    static final transient ReentrantLock reentrantLock = new ReentrantLock();

    public static void setTipVisibility(String key, String val) {
        final ReentrantLock lock = reentrantLock;
        lock.lock();
        try {
            Tip tip1 = new Tip();
            tip1.put(key, val);
            tip = tip1;
        } finally {
            lock.unlock();
        }
    }

    public static void setTipInvisible(String key, String val) {
        final ReentrantLock lock = reentrantLock;
        lock.lock();
        try {
            tip.put(key, val);
        } finally {
            lock.unlock();
        }
    }
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 1000; i++) {
            String key = i + "";
            String val = i + "";
            Thread t1 = new Thread(() -> {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                setTipInvisible(key, val);
            });
            Thread t2 = new Thread(() -> {
                while (tip.getKey() == null && tip.getVal() == null) {
                }
                String a = tip.getKey();
                String b = tip.getVal();
                if (!a.equals(b)) {
                    System.out.println("key:" + a + ",val:" + b);
                }
            });
            t2.start();
            t1.start();
            t1.join();
            t2.join();
            System.out.println("=======================" + (i + 1));
            tip.put(null, null);
        }
    }

}
