package br.com.correios.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utilitarios {

	private static final String DD_MM_YYYY_HH_MM_SS = "dd/MM/yyyy-HH:mm:ss";

	/**
	 * Padrão de data
	 * @param data
	 * @return
	 */
	public static String formataData(Date data) {
		return (new SimpleDateFormat(DD_MM_YYYY_HH_MM_SS)).format(data);
	}
}
