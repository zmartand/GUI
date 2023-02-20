package blockchaintransacciones;

public class EntradaTransaccion {
	public String idSalidaTransaccion; //Hace referencia a salidasTransacciones -> IdTransaccion
	public SalidaTransaccion UTXO; //Contiene la salida de la transacción sin gasto
	
	public EntradaTransaccion(String idSalidaTransaccion) {
		this.idSalidaTransaccion = idSalidaTransaccion;
	}
}
