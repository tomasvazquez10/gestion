package com.gestion.util.mappers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DiaSemanaConverter {

    public static String getDiaSemanaByFecha(Date fecha) {
        Locale locale = new Locale("es");
        DateFormat formatter = new SimpleDateFormat("EEEE", locale);
        String dia = formatter.format(fecha);

        switch (dia){
            case "sábado":
                return "Sabado";
            case "miércoles":
                return "Miercoles";
            default:
                return dia;
        }
    }
}
