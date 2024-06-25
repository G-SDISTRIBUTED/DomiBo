/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.domibo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.JSONObject;

/**
 *
 * @author Pc
 */
public class ManejadorDeConexionesBD {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/socket_database";
    private static final String USER = "postgres";
    private static final String PASS = "123";
    
    public boolean registrarJugadorEnLaBD(String parametros) {
        JSONObject parametrosJson = new JSONObject(parametros);
        String nombre = parametrosJson.getString("nombre");
        String nombreDeUsuario = parametrosJson.getString("nombreDeUsuario");
        String correoElectronico = parametrosJson.getString("correoElectronico");
        String telefono = parametrosJson.getString("telefono");
        String contrasena = parametrosJson.getString("contrasena");
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String query = "INSERT INTO public.users (name,username, email, telefono, password) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, nombre);
            statement.setString(2, nombreDeUsuario);
            statement.setString(3, correoElectronico);
            statement.setString(4, telefono);
            statement.setString(5, contrasena);
            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar el usuario en la BD.");
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean verificarInicioDeSesion(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String query = "SELECT * FROM public.users WHERE username = ? AND password = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, username);
            statement.setString(2, password);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println("Error al iniciar sesión con el usuario en la BD.");
            e.printStackTrace();
            return false;
        }
    }
}
