/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.domibo;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author Pc
 */
public class ServidorSocket implements IEscuchadorDeEventosDelServidor {
    private ServerSocket servidorSocket;
    private ConcurrentHashMap<String, Cliente> clientes;
    private EscuchadorDeConexiones escuchadorDeConexiones;
    private VerificadorDeConexiones verificadorDeConexiones;
    private List<IObservadorDeServidorSocket> observadores;
            
    public ServidorSocket(int puerto){
        try {
            servidorSocket = new ServerSocket(puerto);
            clientes = new ConcurrentHashMap<>();
            escuchadorDeConexiones=new EscuchadorDeConexiones(this,servidorSocket);
            escuchadorDeConexiones.start();
            verificadorDeConexiones=new VerificadorDeConexiones(this);
            verificadorDeConexiones.start();
            observadores= new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    public Cliente obtenerCliente(String token){
        return clientes.get(token);
    }
    
    public void agregarObservador(IObservadorDeServidorSocket observador) {
        observadores.add(observador);
    }

    public void eliminarObservador(IObservadorDeServidorSocket observador) {
        observadores.remove(observador);
    }
    
    private void notificarALosObservadores(String mensaje, String tokenDelCliente) {
        for (IObservadorDeServidorSocket observador : observadores) {
            observador.paqueteRecibido(mensaje,tokenDelCliente);
        }
    }
    
    public ConcurrentHashMap<String, Cliente> obtenerClientes(){
        return this.clientes;
    }

    @Override
    public void clienteConectado(EventoDeClienteConectado evento) {
        Socket clienteSocket=evento.obtenerClienteSocket();
        Cliente cliente = new Cliente(this,clienteSocket);
        String token=cliente.obtenerToken();
        clientes.put(token, cliente);
    }

    @Override
    public void clienteDesconectado(EventoDeClienteDesconectado evento) {
        String tokenDelCliente=evento.obtenerTokenDeCliente();
        Cliente cliente=obtenerCliente(tokenDelCliente);
        cliente.liberarRecursos();
        clientes.remove(tokenDelCliente);
    }

    @Override
    public void mensajeRecibido(EventoDeMensajeRecibido evento) {
        String mensaje = evento.obtenerMensaje();
        String token = evento.obtenerTokenDeCliente();
        notificarALosObservadores(mensaje,token);
    }
}
