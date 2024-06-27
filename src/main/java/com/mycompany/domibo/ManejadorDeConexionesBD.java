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
    private static final String PASS = "password";
    
    public int registrarJugadorEnLaBD(String nombre, String nombreDeUsuario, String correoElectronico, String telefono, String contrasena) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String query = "INSERT INTO public.users (name,username, email, telefono, password) VALUES (?, ?, ?, ?, ?) RETURNING id";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, nombre);
            statement.setString(2, nombreDeUsuario);
            statement.setString(3, correoElectronico);
            statement.setString(4, telefono);
            statement.setString(5, contrasena);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            System.out.println("Error al registrar el usuario en la BD.");
            e.printStackTrace();
            return 0;
        }
    }
    
    public ResultSet verificarInicioDeSesion(String nombreDeUsuario, String contrasena) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String query = "SELECT * FROM public.users WHERE username = ? AND password = ?";
            PreparedStatement statement = conn.prepareStatement(query);
            statement.setString(1, nombreDeUsuario);
            statement.setString(2, contrasena);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet;
            }else{
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error al iniciar sesión con el usuario en la BD.");
            e.printStackTrace();
            return null;
        }
    }
}
