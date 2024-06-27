/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.domibo;

import java.awt.Color;
import java.io.IOException;
import java.io.PrintWriter;
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
    private int puerto;
    private ConcurrentHashMap<String, Cliente> clientes;
    private EscuchadorDeConexiones escuchadorDeConexiones;
    private VerificadorDeConexiones verificadorDeConexiones;
    private FormularioDelServidorSocket formulario;
    private List<IObservadorDeServidorSocket> observadores;
            
    public ServidorSocket(int puerto){
        this.puerto=puerto;
        clientes = new ConcurrentHashMap<>();
        verificadorDeConexiones=new VerificadorDeConexiones(this);
        verificadorDeConexiones.start();
        observadores= new ArrayList<>();
        mostrarFormulario();
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
    
    public void enviarMensajeACliente(String token, Object mensaje) {
        Cliente cliente = clientes.get(token);
        try {
            Socket socket=cliente.obtenerClienteSocket();
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println(mensaje);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void iniciar() {
        try {
        servidorSocket = new ServerSocket(puerto);
        escuchadorDeConexiones=new EscuchadorDeConexiones(this,servidorSocket);
        escuchadorDeConexiones.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void detener() {
        try {
        servidorSocket.close();
        escuchadorDeConexiones.interrupt();
        detenerClientes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void detenerClientes(){
        for (Cliente cliente : clientes.values()) {
            cliente.liberarRecursos();
        }
        clientes.clear();
    }
    
    private void mostrarFormulario() {
        formulario = new FormularioDelServidorSocket(this);
        formulario.setVisible(true);
    }

    @Override
    public void clienteConectado(EventoDeClienteConectado evento) {
        Socket clienteSocket=evento.obtenerClienteSocket();
        Cliente cliente = new Cliente(this,clienteSocket);
        String tokenDelCliente=cliente.obtenerToken();
        clientes.put(tokenDelCliente, cliente);
        formulario.actualizarTextArea("Cliente "+tokenDelCliente+" conectado",Color.GREEN);
    }

    @Override
    public void clienteDesconectado(EventoDeClienteDesconectado evento) {
        String tokenDelCliente=evento.obtenerTokenDeCliente();
        Cliente cliente=obtenerCliente(tokenDelCliente);
        cliente.liberarRecursos();
        clientes.remove(tokenDelCliente);
        formulario.actualizarTextArea("Cliente "+tokenDelCliente+" desconectado",Color.RED);
    }

    @Override
    public void mensajeRecibido(EventoDeMensajeRecibido evento) {
        String mensaje = evento.obtenerMensaje();
        String token = evento.obtenerTokenDeCliente();
        notificarALosObservadores(mensaje,token);
        formulario.actualizarTextArea("Cliente "+token+" mando el mensaje: "+mensaje,Color.GRAY);
    }

    public void enviarMensajeAClientes(String tokenDelCliente, String paqueteSerializadoMover) {
        for (ConcurrentHashMap.Entry<String, Cliente> entry : clientes.entrySet()) {
            String token = entry.getKey();
            Cliente cliente = entry.getValue();
            
            if (!token.equals(tokenDelCliente)) {
                try {
                    Socket socket = cliente.obtenerClienteSocket();
                    PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
                    output.println(paqueteSerializadoMover);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
