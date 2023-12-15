import com.sun.source.tree.WhileLoopTree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
//Carles Ortiz Vidre
public class MainBuses {
    public static void main(String[] args) throws IOException {

        try (Connection con = DriverManager.getConnection("jdbc:sqlite:sqlite.db")) {

            Statement st = con.createStatement();

            // BORRAR SI EXISTEN
            PreparedStatement borrarsiexisten = con.prepareStatement(
                    "DROP TABLE IF EXISTS bonos;" +
                            "DROP TABLE IF EXISTS usuarios;" +
                            "DROP TABLE IF EXISTS bonos_activos;");

            borrarsiexisten.executeUpdate();
            borrarsiexisten.close();

            System.out.println("Base de datos creada.");

            String tablabonos=  "CREATE TABLE IF NOT EXISTS bonos( " +
                    "id_bono INTEGER, " +
                    "tipo VARCHAR(20)," +
                    "duración smallint" +
                    ");";

            st.executeUpdate(tablabonos);



            String tablaUsuarios= "CREATE TABLE IF NOT EXISTS usuarios(" +
                    "DNI integer," +
                    "nombre VARCHAR(20)," +
                    "f_nacimiento DATE," +
                    "ciudad VARCHAR(15)" +
                    ");";

            st.executeUpdate(tablaUsuarios);


            String bonosActivos= "CREATE TABLE IF NOT EXISTS bonos_activos(" +
                    "id_bono INTEGER," +
                    "dni_usuario VARCHAR," +
                    "id_linea INTEGER," +
                    "caducidad DATE" +
                    ");";

            st.executeUpdate(bonosActivos);

            String valoresBono=
                            "INSERT INTO bonos (id_bono,tipo,duración) VALUES (1, 'todo incluido', 1)," +
                                    "(2, 'todo incluido', 7)," +
                            "(3, 'todo incluido', 30)," +
                            "(4, '50%', 1)," +
                            "(5, '50%', 7)," +
                            "(6, '50%', 30);";

            PreparedStatement preparedStatement = con.prepareStatement(valoresBono);
            preparedStatement.executeUpdate();
            System.out.println("Insert de Bonos realizado correctamente.");

            String valoresUsuarios=
                    """
                            INSERT INTO usuarios (DNI,nombre,f_nacimiento,ciudad) VALUES  
                            ('11222333A', 'Patricia Pérez','1990-10-12','Burriana'), 
                            ('22333444B', 'Lia Lorca', '2002-01-31','Castellón'), 
                            ('33444555C', 'Nela Núñez', '2008-28-03','Almazora'), 
                            ('44555666D', 'Jose Jiménez', '2001-15-12','Nules'), 
                            ('55666777E', 'Antonio Aranda', '1989-09-09','Mascarell');
                            
                            """;

            PreparedStatement users = con.prepareStatement(valoresUsuarios);
            users.executeUpdate();
            System.out.println("Insert de usuarios realizado correctamente.");

            String valoresbonosActivos="INSERT INTO bonos_activos (id_bono,dni_usuario, id_linea,caducidad) VALUES " +
                    "(2, '11222333A', 11, '2022-02-22')," +
                    "(5, '11222333A', 31, '2022-02-17')," +
                    "(1, '22333444B', 41, '2022-02-16')," +
                    "(4, '33444555C', 21, '2022-02-16')," +
                    "(6, '44555666D', 51, '2022-03-15')," +
                    "(3, '44555666D', 21, '2022-03-01');";



            st.executeUpdate(valoresbonosActivos);
            System.out.println("Insert de Bonos Activos realizado correctamente.");



            //PRIMERA CONSULTA

            String consulta1 = "SELECT  distinct conductores.Nombre, lineas.id_linea FROM CONDUCTORES " +
                    "INNER JOIN lineas ON lineas.id_conductor = CONDUCTORES.id_conductor " +
                    "INNER JOIN bonos_activos ON bonos_activos.id_linea = lineas.id_linea";
            ResultSet r1 = st.executeQuery(consulta1);

            //MOSTRAR RESULTADO PRIMERA CONSULTA

            while (r1.next()){
                String nombre = r1.getString("Nombre");
                String id_linea = r1.getString("id_linea");

                System.out.println(nombre + " | " + id_linea);
            }
            System.out.println("");
            //SEGUNDA CONSULTA

            String consulta2 = "SELECT  distinct usuarios.nombre,strftime('%Y,%m%d','now')- strftime ('%Y.%m%d',usuarios.f_nacimiento) as edad " +
                    "FROM usuarios " +
                    "INNER JOIN bonos_activos ON bonos_activos.dni_usuario = usuarios.dni " +
                    "INNER JOIN bonos ON bonos.id_bono = bonos_activos.id_bono " +
                    "WHERE bonos.duración = 1";

            ResultSet r2 = st.executeQuery(consulta2);

            //MOSTRAR RESULTADO SEGUNDA CONSULTA

            System.out.println("Consulta 2");
            System.out.println();
            while(r2.next()){
                String nombre = r2.getString("Nombre");
                String edad = r2.getString("edad");

                System.out.println(nombre + " | " + edad);

            }
            //TERCERA CONSULTA

            String consulta3 = "SELECT distinct strftime('%W', caducidad) AS semana, strftime('%Y',caducidad) AS Año, COUNT(*) AS Cantidad_Abonos_Caducan " +
                    "FROM bonos_Activos " +
                    "GROUP BY SEMANA " +
                    "ORDER BY SEMANA";
            ResultSet resultat3 = st.executeQuery(consulta3);

            //MOSTRAR RESULTADO TERCERA CONSULTA

            System.out.println("Consulta 3");
            System.out.println();

            while (resultat3.next()){
                String semana = resultat3.getString("semana");
                String cantidad_abonos_caducan = resultat3.getString("cantidad_abonos_caducan");
                String año = resultat3.getString("año");

                if (Integer.parseInt(cantidad_abonos_caducan)>1) System.out.println("La semana " + semana + "del año" + año +" caducan" + cantidad_abonos_caducan);

                else if (Integer.parseInt(cantidad_abonos_caducan)==1) System.out.println("La semana " + semana + "del año" + año +" caducan" + cantidad_abonos_caducan);


                System.out.println();
            }


        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}
//Carles Ortiz Vidre
