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
public class EventoDeClienteDesconectado extends EventObject {
    private Socket clienteSocket;
    private String tokenDeCliente;

    public EventoDeClienteDesconectado(Object fuente,Socket clienteSocket, String tokenDeCliente) {
        super(fuente);
        this.clienteSocket = clienteSocket;
        this.tokenDeCliente=tokenDeCliente;
    }
    
    public EventoDeClienteDesconectado(Object fuente,Socket clienteSocket) {
        super(fuente);
        this.clienteSocket = clienteSocket;
        this.tokenDeCliente=null;
    }
    
    public Socket obtenerClienteSocket(){
        return clienteSocket;
    }
    
    public String obtenerTokenDeCliente(){
        return tokenDeCliente;
    }

    void modicarTokenDeCliente(String token) {
        this.tokenDeCliente=token;
    }
}
