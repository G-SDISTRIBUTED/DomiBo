/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.domibo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Pc
 */
public class EscuchadorDeConexiones extends Thread {
    private IEscuchadorDeEventosDelServidor escuchador;
    private ServerSocket servidorSocket;

    public EscuchadorDeConexiones(IEscuchadorDeEventosDelServidor escuchador, ServerSocket servidorSocket) {
        this.servidorSocket=servidorSocket;
        this.escuchador=escuchador;
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                Socket clienteSocket = servidorSocket.accept();
                EventoDeClienteConectado evento = new EventoDeClienteConectado(this, clienteSocket);
                escuchador.clienteConectado(evento);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
