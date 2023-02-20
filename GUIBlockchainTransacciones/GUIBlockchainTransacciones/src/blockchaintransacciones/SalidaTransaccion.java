package blockchaintransacciones;

import java.security.PublicKey;

public class SalidaTransaccion {
	public String id;
	public PublicKey destinatario; //también conocido como el nuevo propietario de estas monedas.
	public float valor; //la cantidad de monedas que poseén
	public String idTransaccionPadre; //el id de la transacción que fue creado en esta salida
	
	//Constructor
	public SalidaTransaccion(PublicKey destinatario, float valor, String idTransaccionPadre) {
		this.destinatario = destinatario;
		this.valor = valor;
		this.idTransaccionPadre = idTransaccionPadre;
		this.id = Hash.aplicarSha256(Hash.obtenerCadenaDesdeClave(destinatario)+Float.toString(valor)+idTransaccionPadre);
	}
	
	//Se comprueba si te pertenece la moneda
	public boolean esMia(PublicKey clavePublica) {
		return (clavePublica == destinatario);
	}
	
}
