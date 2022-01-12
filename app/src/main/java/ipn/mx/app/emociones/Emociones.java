package ipn.mx.app.emociones;

public class Emociones {

    int id_emocion;
    String emocion_texto;

    public Emociones (){

    }

    public Emociones(int id_emocion, String emocion_texto) {
        this.id_emocion = id_emocion;
        this.emocion_texto = emocion_texto;
    }

    public int getId_emocion() {
        return id_emocion;
    }

    public void setId_emocion(int id_emocion) {
        this.id_emocion = id_emocion;
    }

    public String getEmocion_texto() {
        return emocion_texto;
    }

    public void setEmocion_texto(String emocion_texto) {
        this.emocion_texto = emocion_texto;
    }
}
