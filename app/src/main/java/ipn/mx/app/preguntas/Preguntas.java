package ipn.mx.app.preguntas;

public class Preguntas {

    int id_preguntas;
    String texto_pregunta;

    public Preguntas() {

    }

    public Preguntas(int id_preguntas, String texto_pregunta) {
        this.id_preguntas = id_preguntas;
        this.texto_pregunta = texto_pregunta;
    }

    public int getId_preguntas() {
        return id_preguntas;
    }

    public void setId_preguntas(int id_preguntas) {
        this.id_preguntas = id_preguntas;
    }

    public String getTexto_pregunta() {
        return texto_pregunta;
    }

    public void setTexto_pregunta(String texto_pregunta) {
        this.texto_pregunta = texto_pregunta;
    }
}
