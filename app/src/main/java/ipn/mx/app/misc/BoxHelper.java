package ipn.mx.app.misc;

public class BoxHelper<T>{
    T inner;
    public BoxHelper(){
    }

    public T get() {
        return inner;
    }

    public void set(T inner) {
        this.inner = inner;
    }
}
