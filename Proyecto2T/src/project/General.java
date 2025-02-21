package project;

//Método main que ejecuta el programa.
public class General {
  public static void main(String[] args) {

      String apiKey = "&key=d9eac312d82b4e5188f72524250902";
      String location = "";

      Meteo run = new Meteo(apiKey, location);

      run.Run();

  }
}
