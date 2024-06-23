/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.domibo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Pc
 */
public class VerificadorDeConexiones  extends Thread {
    private ServidorSocket servidorSocket;

    public VerificadorDeConexiones(ServidorSocket servidorSocket) {
        this.servidorSocket=servidorSocket;
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                verificarConexiones();
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void verificarConexiones() {
        ConcurrentHashMap<String, Cliente> clientes = servidorSocket.obtenerClientes();
        clientes.forEach((token, cliente) -> {
            Socket clienteSocket=cliente.obtenerClienteSocket();
            InetAddress direccionDeRed = clienteSocket.getInetAddress();
            try {
                if (!direccionDeRed.isReachable(2000)) {
                    EventoDeClienteDesconectado evento = new EventoDeClienteDesconectado(this, clienteSocket,token);
                    servidorSocket.clienteDesconectado(evento);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
