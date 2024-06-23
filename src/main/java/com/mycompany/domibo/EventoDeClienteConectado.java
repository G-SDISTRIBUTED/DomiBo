/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.domibo;

import java.net.Socket;
import java.util.EventObject;

/**
 *
 * @author Pc
 */
public class EventoDeClienteConectado extends EventObject {
    private Socket clienteSocket;

    public EventoDeClienteConectado(Object fuente,Socket clienteSocket) {
        super(fuente);
        this.clienteSocket = clienteSocket;
    }
    
    public Socket obtenerClienteSocket(){
        return clienteSocket;
    }
}
