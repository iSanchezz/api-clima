package project;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class Meteo {
    Scanner sc = new Scanner(System.in);
    String url = "https://api.weatherapi.com/v1/";
    String language = "&lang=es"; // Esto es necesario para construir la URL
    String apiKey;
    String location;
    int option;
    int days;

    // Constructor
    public Meteo(String apiKey, String location) {
        this.apiKey = apiKey;
        this.location = location;

    }

    // Imprimir Menú
    int menu() {

        try {

            System.out.println(" ");
            System.out.println("Menú");
            System.out.println("Elige una opción");
            System.out.println("1. Tiempo actual");
            System.out.println("2. Predicción Meteorológica");
            System.out.println("3. Historial de la ubicación");

            option = sc.nextInt();
            sc.nextLine(); // Esto consume el salto de linea que hace que location no funcione
                           // correctamente
            boolean isValid = option > 0 && option <= 3;

            if (!isValid) {
                System.out.println("Esa opción no es válida");

            } else {
                System.out.println("Introduce la ubicación que quieres consultar");
                location = sc.nextLine();
            }
            // Esto solucionará el problema de los espacios en ciudades cuyo nombre tiene
            // más de una palabra
            location = location.replace(" ", "%20");

        } catch (java.lang.StringIndexOutOfBoundsException | java.util.InputMismatchException e) {
            System.out.println("Ha ocurrido un error: No se puede reconocer la opción introducida. ");
            option = 0;
        }
        return option;
    }

    /*
     * Método para hacer una petición a la API
     * Este método iba a estar en ApiCalls pero no puedo hacerlo funcionar desde
     * ahí.
     */
    private String executeRequest(String requestURL) {
        StringBuilder response = new StringBuilder();
        try {
            URL link = new URL(requestURL);
            HttpURLConnection con = (HttpURLConnection) link.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);

            }

        } catch (Exception e) {
            e.getMessage();
        }
        return response.toString();

    }

    // Método para conseguir el tiempo actual en una ubicación
    String getCurrentWeather() {
        String request = url + "current.json?q=" + location + language + apiKey;
        String response = executeRequest(request);

        return response;
    }

    // Predicción meteorológica
    String getForecast() {
        boolean isValid = false;
        String input = "";
        System.out.println("Introduce la cantidad de días (0 a 3)");
        System.out.println("Si quieres consultar el tiempo que va a hacer en lo que resta de día introduce 0");

        do {
            days = sc.nextInt();

            if (days >= 0 && days <= 3) {
                input = "&days=" + days;
                isValid = true;

            } else if (days > 3) {
                System.out.println("Vas a tener que pagar para eso :/");
                System.out.println("Vuelve a introducir un número de días entre 1 y 3");

            } else {
                System.out.println("Esa no es una entrada válida");
                System.out.println("Introduce un número de días entre 1 y 3");
            }

        } while (!isValid);

        String request = url + "forecast.json?q=" + location + input + language + apiKey;
        String response = executeRequest(request);

        return response;
    }

    // Historial meteorológico en una ubicación
    String getHistory() {
        try {

            boolean isValid = false;
            System.out.println("Introduce la cantidad de días (1 a 7)");
            System.out.println("También puedes introducir 0 para consultar el tiempo que hemos tenido hoy");

            do {
                days = sc.nextInt();

                if (days >= 0 && days <= 7) {
                    isValid = true;

                } else if (days > 7) {
                    System.out.println("Vas a tener que pagar para eso :(");
                    System.out.println("Vuelve a introducir un número de días entre 1 y 7");

                } else {
                    System.out.println("Esa no es una entrada válida");
                    System.out.println("Introduce un número de días entre 1 y 7");
                }

            } while (!isValid);
            LocalDate today = LocalDate.now();
            LocalDate daysTime = today.minusDays(days);

            String request = url + "history.json?q=" + location + "&dt=" + daysTime + language + apiKey;

            String response = executeRequest(request);
            return response;
        } catch (java.lang.StringIndexOutOfBoundsException | java.util.InputMismatchException e) {
            System.out.println("Error. No se puede reconocer el número de días introducido.");

            return "Error";
        }

    }

    void output(String data) {

        // Estos son todos los datos que en algún momento se mostrarán por consola
        String weekDay = "";
        String lastUpdated = extractValue(data, "\"last_updated\":", ",");
        String locationName = extractValue(data, "\"name\":\"", "\"");
        String region = extractValue(data, "\"region\":\"", "\"");
        String country = extractValue(data, "\"country\":\"", "\"");
        String temperature = extractValue(data, "\"temp_c\":", ",");
        String humidity = extractValue(data, "\"humidity\":", ",");
        String avgHumidity = extractValue(data, "\"avghumidity\":", ",");
        String feelsLike = extractValue(data, "\"feelslike_c\":", ",");
        String conditions = extractValue(data, "\"text\":\"", "\"");
        String wind = extractValue(data, "\"wind_kph\":", ",");
        String maxWind = extractValue(data, "\"maxwind_kph\":", ",");
        String windDir = extractValue(data, "\"wind_dir\":\"", "\"");
        String minTemp = extractValue(data, "\"mintemp_c\":", ",");
        String maxTemp = extractValue(data, "\"maxtemp_c\":", ",");
        String avgTemp = extractValue(data, "\"avgtemp_c\":", ",");
        String totalPrecip = extractValue(data, "\"totalprecip_mm\":", ",");

        /*
         * Esto hace que si en lugar de historial elegimos prediccion, invierta el
         * calculo de los dias en LocalDate
         * De esta forma no tenemos que repetir el switch de abajo para ambos métodos
         */
        if (option == 2) {
            days = -days;
        }

        switch (LocalDate.now().minusDays(days).getDayOfWeek()) {
            case MONDAY -> weekDay = "Lunes";

            case TUESDAY -> weekDay = "Martes";

            case WEDNESDAY -> weekDay = "Miercoles";

            case THURSDAY -> weekDay = "Jueves";

            case FRIDAY -> weekDay = "Viernes";

            case SATURDAY -> weekDay = "Sabado";

            case SUNDAY -> weekDay = "Domingo";

        }

        switch (option) {
            case 1 -> {
                System.out.println(" ");
                System.out.println(
                        "Ubicación: " + locationName + ", " + region + ", " + country + ". Fecha y hora: "
                                + lastUpdated);
                System.out.println(" ");
                System.out.println("Hace un tiempo " + conditions);
                System.out.println(" ");
                System.out.println("Temperatura: " + temperature + "ºC. Sensación térmica " + feelsLike + "ºC");
                System.out.println("Humedad: " + humidity + "%.");
                System.out.println("Viento: " + wind + " km/h dirección " + windDir);

            }
            case 2 -> {
                System.out.println(
                        "Predicción del tiempo en " + locationName + " " + region + " " + country
                                + " desde hoy hasta el " + weekDay + " "
                                + (LocalDate.now().plusDays(-days)).getDayOfMonth());

                System.out.println();
                System.out.println("El tiempo va a estar " + conditions);
                System.out.println(" ");
                System.out.println("Temperaturas");
                System.out
                        .println("Máxima: " + maxTemp + "ºC || Mínima: " + minTemp + "ºC || Media: " + avgTemp + "ºC");
                System.out.println(" ");
                System.out.println("Humedad Media: " + avgHumidity + "%");
                System.out.println(" ");
                System.out.println("Precipitación prevista: " + totalPrecip + "mm.");
                System.out.println(" ");
                System.out.println("Viento");
                System.out.println("Velocidad máxima: " + maxWind + "km/h");

            }
            case 3 -> {
                // Me di cuenta tarde que el .JSON te devuelve directamente el average
                // temperature pero despues de haberlo calculado ya no lo voy a borrar

                // TEMPERATURAS
                // Temperatura media
                /*
                 * double total = 0;
                 * int count = 0;
                 * int startIndex = 0;
                 * 
                 * while ((startIndex = data.indexOf("\"temp_c\":", startIndex)) != -1) {
                 * startIndex += "\"temp_c\":".length();
                 * int endIndex = data.indexOf(",", startIndex);
                 * 
                 * if (endIndex == -1) {
                 * endIndex = data.length();
                 * }
                 * String tempString = data.substring(startIndex, endIndex).trim();
                 * total += Double.parseDouble(tempString);
                 * count++;
                 * }
                 * double avgTemp = total / count;
                 */

                // Mostramos los datos recogidos por pantalla
                if (days == 0) {
                    System.out.println("Historial del tiempo en " + locationName + " en lo que va de día.");

                } else {
                    System.out.println(
                            "Historial del tiempo en " + locationName + " desde el " +
                                    weekDay + " " + LocalDate.now().minusDays(days).getDayOfMonth() + " hasta hoy");
                }

                System.out.println();
                System.out.println("Temperaturas:");
                System.out
                        .println("Máxima: " + maxTemp + "ºC || Mínima: " + minTemp + "ºC || Media: " + avgTemp + "ºC");
                System.out.println(" ");
                System.out.println("Humedad Media: " + avgHumidity + "%");
                System.out.println(" ");
                System.out.println("Precipitación total: " + totalPrecip + "mm.");
                System.out.println(" ");
                System.out.println("Viento: ");
                System.out.println("Velocidad máxima: " + maxWind + "km/h");

            }

        }

    }

    // Este es el método que usaremos para extraer los valores del .JSON
    String extractValue(String data, String key, String delimiter) {
        int startIndex = data.indexOf(key) + key.length();
        int endIndex = data.indexOf(delimiter, startIndex);
        return data.substring(startIndex, endIndex);
    }

    void Run() {

        try {

            String data = "";
            String ask;
            boolean repeat;
            boolean advance = false;
            do {
                do {
                    switch (menu()) {
                        case 1 -> {
                            data = getCurrentWeather();
                            advance = true;
                        }

                        case 2 -> {
                            data = getForecast();
                            advance = true;
                        }

                        case 3 -> {
                            data = getHistory();
                            advance = true;
                        }

                        default -> {
                            System.out.println("Vuelve a introducir la opción");

                        }
                    }

                } while (!advance);

                advance = false;

                output(data);

                System.out.println("Quieres hacer otra consulta?");

                ask = sc.next();
                ask = ask.toLowerCase();
                repeat = ask.equals("si");

            } while (repeat);
            System.out.println(" ");
            System.out.println("Gracias por utilizar este programa!");
            System.out.println("Hasta la próxima");
            System.out.println(" ");

        } catch (java.lang.StringIndexOutOfBoundsException e) {

        }
    }
}
