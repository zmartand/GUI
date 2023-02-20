package blockchaintransacciones;
import java.security.*;
import java.util.ArrayList;

public class Transaccion {
	
	public String idTransaccion; //Contiene un hash de la transacción
	public PublicKey remitente; //Dirección/clave pública de los remitentes.
	public PublicKey destinatario; //Dirección/clave pública de los destinatarios.
	public float valor; //Contiene la cantidad que queremos enviar al destinatario.
	public byte[] firma; //Para prevenir que nadie más gaste dinero de nuestra catera.
	
	public ArrayList<EntradaTransaccion> entradas = new ArrayList<EntradaTransaccion>();
	public ArrayList<SalidaTransaccion> salidas = new ArrayList<SalidaTransaccion>();
	
	private static int secuencia = 0; //Una cuenta aproximada de la cantidad de transacciones que han sido generadas 
	
	// Constructor: 
	public Transaccion(PublicKey del, PublicKey al, float valor,  ArrayList<EntradaTransaccion> entradas) {
		this.remitente = del;
		this.destinatario = al;
		this.valor = valor;
		this.entradas = entradas;
	}
	
	public boolean procesarTransaccion() {
		
		if(verificarFirma() == false) {
			System.out.println("#La firma de la transacción falló en la verificación");
			return false;
		}
				
		//Obtención de las entradas de la transaccion (Asegurándose de que son sin gasta):
		for(EntradaTransaccion i : entradas) {
			i.UTXO = Principal.UTXOs.get(i.idSalidaTransaccion);
		}

		//Comprobación si latransacción es válida:
		if(obtenerValorEntradas() < Principal.transaccionMinima) {
			System.out.println("Las entradas de la transacción son demasiado pequeñas: " + obtenerValorEntradas());
			System.out.println("Por favor, introduzca una cantidad mayor a " + Principal.transaccionMinima);
			return false;
		}
		
		//Generación de las salidas de la transacción:
		float dejadoSobre = obtenerValorEntradas() - valor; //Obtención del valor de las entradas de lo que queda del cambio:
		idTransaccion = calcularHash();
		salidas.add(new SalidaTransaccion( this.destinatario, valor, idTransaccion)); //Envío del valor al destinatario
		salidas.add(new SalidaTransaccion( this.remitente, dejadoSobre,idTransaccion)); //Devolución de lo que queda del 'cambio' al remitente		
				
		//Añade salidas a la listas sin gasto
		for(SalidaTransaccion o : salidas) {
			Principal.UTXOs.put(o.id , o);
		}
		
		//Elimina las enradas de lastransacciones de las listas UTXO com pagadas:
		for(EntradaTransaccion i : entradas) {
			if(i.UTXO == null) continue; //Si la transacción no se ha encontrado se omite 
			Principal.UTXOs.remove(i.UTXO.id);
		}
		
		return true;
	}
	
	public float obtenerValorEntradas() {
		float total = 0;
		for(EntradaTransaccion i : entradas) {
			if(i.UTXO == null) continue; //Si no se encuetra la transacción se omite. Este comportamiento podría no ser óptimo.
			total += i.UTXO.valor;
		}
		return total;
	}
	
	public void generarFirma(PrivateKey clavePrivada) {
		String datos = Hash.obtenerCadenaDesdeClave(remitente) + Hash.obtenerCadenaDesdeClave(destinatario) + Float.toString(valor)	;
		firma = Hash.aplicarFirmaECDSA(clavePrivada,datos);		
	}
	
	public boolean verificarFirma() {
		String datos = Hash.obtenerCadenaDesdeClave(remitente) + Hash.obtenerCadenaDesdeClave(destinatario) + Float.toString(valor)	;
		return Hash.verificarFirmaECDSA(remitente, datos, firma);
	}
	
	public float obtenerValorSalidas() {
		float total = 0;
		for(SalidaTransaccion o : salidas) {
			total += o.valor;
		}
		return total;
	}
	
	private String calcularHash() {
		secuencia++; //Incremento de la secuencia para evitar que 2 transaciones idénticas tengan el mismo hash
		return Hash.aplicarSha256(
				Hash.obtenerCadenaDesdeClave(remitente) +
				Hash.obtenerCadenaDesdeClave(destinatario) +
				Float.toString(valor) + secuencia
				);
	}
}
