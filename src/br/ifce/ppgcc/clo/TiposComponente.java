package br.ifce.ppgcc.clo;

public enum TiposComponente {
	
	TEXTO(new String[]{"name", "enabled", "visibled", "text", "position", "zindex"}),
	BOTAO(new String[]{"name", "enabled", "visibled", "label", "position", "zindex"}),
	IMAGEM(new String[]{"name", "enabled", "visibled", "source", "position", "zindex"}),
	AUDIO(new String[]{"name", "enabled", "visibled", "source", "position", "zindex", "startTime", "stopTime"}),
	VIDEO(new String[]{"name", "enabled", "visibled", "source", "position", "zindex", "startTime", "stopTime"});
	
	private String[] atributos;  
	
	private TiposComponente(String[] atributos) {
		this.atributos = atributos;
	}
	
	public String[] getAtributos() {
		return atributos;
	}	
}
