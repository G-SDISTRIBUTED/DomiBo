/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.domibo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 *
 * @author Pc
 */
public class EscuchadorDeEventos extends Thread {
    private IEscuchadorDeEventosDeClientes escuchador;
    private Socket clienteSocket;

    public EscuchadorDeEventos(IEscuchadorDeEventosDeClientes escuchador, Socket clienteSocket) {
        this.clienteSocket=clienteSocket;
        this.escuchador=escuchador;
    }
    
    @Override
    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            String mensaje;
            while ((mensaje = input.readLine()) != null) {
                EventoDeMensajeRecibido evento = new EventoDeMensajeRecibido(this, mensaje);
                escuchador.mensajeRecibido(evento);
            }
            if(mensaje == null){
                EventoDeClienteDesconectado evento = new EventoDeClienteDesconectado(this, clienteSocket);
                escuchador.clienteDesconectado(evento);
            }
        } catch (IOException e) {
            EventoDeClienteDesconectado evento = new EventoDeClienteDesconectado(this, clienteSocket);
            escuchador.clienteDesconectado(evento);
        } finally {
            try {
                clienteSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
