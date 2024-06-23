/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.domibo;

import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Pc
 */
public class Cliente implements IEscuchadorDeEventosDeClientes {
    private Socket clienteSocket;
    private String token;
    private EscuchadorDeEventos escuchadorDeEventos;
    private IEscuchadorDeEventosDelServidor escuchadorDeConexiones;

    public Cliente(IEscuchadorDeEventosDelServidor escuchadorDeConexiones, Socket clienteSocket) {
        this.clienteSocket = clienteSocket;
        escuchadorDeEventos=new EscuchadorDeEventos(this,clienteSocket);
        InetAddress direccionDeIp = this.clienteSocket.getInetAddress();
        String ipDeCliente = direccionDeIp.getHostAddress();
        token=generarToken(ipDeCliente);
        this.escuchadorDeConexiones=escuchadorDeConexiones;
        escuchadorDeEventos.start();
    }

    public static String generarToken(String ip) {
        try {
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String dateTimeString = now.format(formatter);
            String input = ip + dateTimeString;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generando el token", e);
        }
    }

    @Override
    public void mensajeRecibido(EventoDeMensajeRecibido evento) {
        String token=obtenerToken();
        evento.modicarTokenDeCliente(token);
        escuchadorDeConexiones.mensajeRecibido(evento);
    }

    @Override
    public void clienteDesconectado(EventoDeClienteDesconectado evento) {
        String token=obtenerToken();
        evento.modicarTokenDeCliente(token);
        escuchadorDeConexiones.clienteDesconectado(evento);
    }

    public String obtenerToken() {
        return this.token;
    }
    
    public Socket obtenerClienteSocket() {
        return this.clienteSocket;
    }

    void liberarRecursos() {
        try {
            if (clienteSocket != null && !clienteSocket.isClosed()) {
                clienteSocket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        escuchadorDeEventos.interrupt();
    }
}
