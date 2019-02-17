### Exchanger  
◆ 示例    
```
public static void main(String[] args) throws Exception {
    Exchanger<String> exchanger = new Exchanger<>();
    Car car = new Car(exchanger);
    Bike bike = new Bike(exchanger);
    LogTrack.w("Main start!");
    car.start();
    bike.start();
    Thread.sleep(5000);
}

private static final class Car extends Thread {
    private Exchanger<String> exchanger;

    public Car(Exchanger<String> exchanger) {
        super();
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
            LogTrack.w(getClass().getSimpleName() + ": " + exchanger.exchange("Car"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

private static final class Bike extends Thread {
    private Exchanger<String> exchanger;

    public Bike(Exchanger<String> exchanger) {
        super();
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
            LogTrack.w(getClass().getSimpleName() + ": " + exchanger.exchange("Bike"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```