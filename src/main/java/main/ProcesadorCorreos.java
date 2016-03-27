package main;

import adaptador.AdaptadorSymphony;
import config.ConfiguracionException;
import config.ConfiguracionProcesadorCorreos;
import lector.CuentaCorreo;
import lector.LectorCorreos;
import lector.LectorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

public class ProcesadorCorreos {

	private static ConfiguracionProcesadorCorreos configuracion = null;
	private static final Logger logger = LogManager.getLogger(ProcesadorCorreos.class);
	private AdaptadorSymphony adaptador;
	private ArrayList<CuentaCorreo> cuentas;

	public static ConfiguracionProcesadorCorreos getConfiguracion() throws ConfiguracionException {
		if (configuracion == null) {
			configuracion = new ConfiguracionProcesadorCorreos();
		}
		return configuracion;
	}

	public static Logger getLogger() {
		return logger;
	}

	private void init() throws ConfiguracionException {
		cuentas = ProcesadorCorreos.getConfiguracion().leerCorreos();
		adaptador = ProcesadorCorreos.getConfiguracion().leerAdaptador();
	}

	public void lanzarLectores() {
		for (CuentaCorreo cuenta : cuentas) {
			try {
				LectorCorreos lector = new LectorCorreos(cuenta, adaptador);
				lector.leerInbox();
			} catch (LectorException e) {
				ProcesadorCorreos.logger.info("error al procesar lector: " + e.getMessage());
			}
		}
	}

	public static void main(String[] args) {
		logger.info("inicio");
		ProcesadorCorreos pc = new ProcesadorCorreos();
		try {
			pc.init();
		} catch (ConfiguracionException e) {
			ProcesadorCorreos.logger.info("error: " + e.getMessage());
			e.printStackTrace();
		}
		pc.lanzarLectores();
		logger.info("final");
	}

}
