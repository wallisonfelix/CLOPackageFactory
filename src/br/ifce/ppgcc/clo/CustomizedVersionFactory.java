package br.ifce.ppgcc.clo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.JSONArray;
import org.json.JSONObject;

public class CustomizedVersionFactory {
	
	private static final String DIRECTORY_NAME = "D:/clo";  
	
    public static void main(String[] args) throws Exception {
        
    	String nomeVersaoCustomizada = (String) args[0];
    	int qtdComponentesCustomizados = Integer.parseInt(args[1]);
    	int qtdArqMidiasCustomizados = Integer.parseInt(args[2]);
    	
    	System.out.println(new Date());
    	System.out.println("### Iniciando a geração da Versão Customizada com: ###");
    	System.out.println("-> " + qtdComponentesCustomizados + " Componentes Customizados");
    	System.out.println("-> " + qtdArqMidiasCustomizados + " Arquivos de Mídia Customizados");
    	
    	System.out.println("### Nome da Versão Customizada: " + nomeVersaoCustomizada + "##");
    	
    	ZipOutputStream customizedVersion = new ZipOutputStream(new FileOutputStream(DIRECTORY_NAME + "/new_" + nomeVersaoCustomizada + ".zip"));
    	byte[] arrayBytes;
        int index;
    	
        //Cria o nomeVersaoCustomizada.json            
        JSONObject componentesCustomizadoJson = gerarComponentesCustomizadoJson(nomeVersaoCustomizada, qtdComponentesCustomizados, qtdArqMidiasCustomizados);
        int qtdCenas = componentesCustomizadoJson.getJSONArray("scenes").length();
        
        //Adiciona os Arquivos de Mídia em components
        for (int j = 0; j < qtdCenas; j++) {
        	JSONArray componentesCenaJson = (JSONArray) (((JSONObject) (componentesCustomizadoJson.getJSONArray("scenes").get(j))).getJSONArray("components"));
        	
        	for (int k = 0; k < componentesCenaJson.length(); k++) {
        		JSONObject componenteCenaJson = (JSONObject) componentesCenaJson.get(k);
        		if (componenteCenaJson.has("source")) {
        			String source = componenteCenaJson.getString("source");
        			String[] sourceTokens = source.split("_");
        			if (sourceTokens[0].equals("")) {
	        			FileInputStream fisArquivoMidia = new FileInputStream(DIRECTORY_NAME + "/arquivos_midia/" + sourceTokens[sourceTokens.length - 1]);
	        			String newSource = "new_" + source.substring(1);
	        			componenteCenaJson.put("source", newSource);
	        			customizedVersion.putNextEntry(new ZipEntry("components/" + newSource));
	        			arrayBytes = new byte[1024];
	                	index = 0;    
	                    while ((index = fisArquivoMidia.read(arrayBytes)) > 0) {
	                        customizedVersion.write(arrayBytes, 0, index);
	                    }
	                    
	                    //Finalizando a inclusão de um Arquivo de Mídia
	                    fisArquivoMidia.close();
        			} else {
        				componenteCenaJson.put("source", Paths.get(source).getFileName().toString());
        			}
        		}
        	}
        }
        
        //Finaliza a criação do nomeVersaoCustomizada.json na Versão Customizada
        PrintWriter componentesCustomizadoFile = new PrintWriter(DIRECTORY_NAME + "/customizado/" + nomeVersaoCustomizada + ".json", "UTF-8");
    	componentesCustomizadoFile.write(componentesCustomizadoJson.toString());
    	componentesCustomizadoFile.close();
    	//Adiciona o nomeVersaoCustomizada.json na Versão Customizada
    	FileInputStream fisComponentesCustomizadosJson = new FileInputStream(DIRECTORY_NAME + "/customizado/" + nomeVersaoCustomizada + ".json");
    	customizedVersion.putNextEntry(new ZipEntry(nomeVersaoCustomizada + ".json"));
    	arrayBytes = new byte[1024];
    	index = 0;    
        while ((index = fisComponentesCustomizadosJson.read(arrayBytes)) > 0) {
            customizedVersion.write(arrayBytes, 0, index);
        }
          
        //Adiciona o token.txt na Versão Customizada
    	FileInputStream fisTokenTxt = new FileInputStream(DIRECTORY_NAME + "/customizar/token.txt");
    	customizedVersion.putNextEntry(new ZipEntry("token.txt"));
    	arrayBytes = new byte[1024];
    	index = 0;    
        while ((index = fisTokenTxt.read(arrayBytes)) > 0) {
            customizedVersion.write(arrayBytes, 0, index);
        }
        
        //Finalizando
        customizedVersion.close();
        fisComponentesCustomizadosJson.close();
        fisTokenTxt.close();
        
        File diretorioTemporario = new File(DIRECTORY_NAME + "/temp/");
        limparDiretorio(diretorioTemporario);
        File diretorioCustomizar = new File(DIRECTORY_NAME + "/customizar/");
        limparDiretorio(diretorioCustomizar);
        File diretorioCustomizado = new File(DIRECTORY_NAME + "/customizado/");
        limparDiretorio(diretorioCustomizado);
        
        System.out.println(new Date());
    	System.out.println("### Finalizando a geração da Versão Customizada ###");
    }
    
    private static JSONObject gerarComponentesCustomizadoJson(String nomeVersaoCustomizada, int qtdComponentesCustomizados, int qtdArqMidiasCustomizados) throws Exception {
    	StringBuilder linhaComponentesJsonOriginal = new StringBuilder();
    	for (String linha : Files.readAllLines(Paths.get(DIRECTORY_NAME + "/customizar/" + nomeVersaoCustomizada + ".json"))) {
    		linhaComponentesJsonOriginal.append(linha);
    	}
    	
    	JSONObject componentesJson = new JSONObject(linhaComponentesJsonOriginal.toString());
    	JSONArray cenasJson = componentesJson.getJSONArray("scenes");
    	int qtdCenas = cenasJson.length();
    	
    	int qtdComponentesCustomizadosPorCena = Math.floorDiv(qtdComponentesCustomizados, qtdCenas);
    	int qtdComponentesCustomizadosSobrando = Math.floorMod(qtdComponentesCustomizados, qtdCenas);
    	int qtdArqMidiasCustomizadosPorCena = Math.floorDiv(qtdArqMidiasCustomizados, qtdCenas);
    	int qtdArqMidiasCustomizadosSobrando = Math.floorMod(qtdArqMidiasCustomizados, qtdCenas);
 
    	for (int i = 1; i <= qtdCenas; i++) {
    		JSONArray componentesCenaJson = cenasJson.getJSONObject(i - 1).getJSONArray("components");
    		
    		int qtdComponentesCena = componentesCenaJson.length();
    		int qtdArqMidiasCena = 0;
    		for (int j = 0; j < qtdComponentesCena; j++) {
    			if (((JSONObject) componentesCenaJson.get(j)).has("source")) {
    				qtdArqMidiasCena++;
    			}
    		}
    		
    		int qtdComponentesCustomizadosCena;
    		int qtdArqMidiasCustomizadosCena;
    		if (i < qtdCenas) {
    			if (qtdComponentesCustomizadosSobrando > 0 && qtdComponentesCustomizadosPorCena < qtdComponentesCena) { 
    				qtdComponentesCustomizadosCena = qtdComponentesCustomizadosPorCena + 1;
    				qtdComponentesCustomizadosSobrando--;
    			} else {
    				qtdComponentesCustomizadosCena = qtdComponentesCustomizadosPorCena;
    			}
    			if (qtdArqMidiasCustomizadosSobrando > 0 && qtdArqMidiasCustomizadosPorCena < qtdArqMidiasCena) {
    				qtdArqMidiasCustomizadosCena = qtdArqMidiasCustomizadosPorCena + 1;
    				qtdArqMidiasCustomizadosSobrando--;
    			} else {
    				qtdArqMidiasCustomizadosCena = qtdArqMidiasCustomizadosPorCena;
    			}
    		} else {
    			qtdComponentesCustomizadosCena = qtdComponentesCustomizadosPorCena + qtdComponentesCustomizadosSobrando;
    			qtdArqMidiasCustomizadosCena = qtdArqMidiasCustomizadosPorCena + qtdArqMidiasCustomizadosSobrando;
    		}
    		
    		int qtdArqMidiasModificadosCena = 0;
			for (int j = 0; j < qtdComponentesCena; j++) {
				if (qtdArqMidiasModificadosCena == qtdArqMidiasCustomizadosCena) {
					break;
				}
				
				JSONObject componenteJson = (JSONObject) componentesCenaJson.get(j);
				if (componenteJson.has("source")) {
					String source = Paths.get((String) componenteJson.get("source")).getFileName().toString();
					componenteJson.put("source", "_" + source);
    				qtdArqMidiasModificadosCena++;    				
    			}
    		}
			
			int qtdComponentesModificadosCena = qtdArqMidiasCustomizadosCena;
			for (int j = 0; j < qtdComponentesCena; j++) {
				if (qtdComponentesModificadosCena == qtdComponentesCustomizadosCena) {
					break;
				}
				
				JSONObject componenteJson = (JSONObject) componentesCenaJson.get(j);
				if (!componenteJson.has("source")) {
					if (componenteJson.has("text")) {
						String text = (String) componenteJson.get("text");
						componenteJson.put("text", "new_" + text);
						qtdComponentesModificadosCena++;
					} else if (componenteJson.has("label")) {
						String label = (String) componenteJson.get("label");
						componenteJson.put("label", "new_" + label);
						qtdComponentesModificadosCena++;
					}								    				
    			}
    		}
    	}
				
    	return componentesJson;
    }
    
    public static void limparDiretorio(File diretorio) {
        File[] files = diretorio.listFiles();
        
        if (files != null) {
            for(File file : files) {
                if(file.isDirectory()) {
                    limparDiretorio(file);
                } else {
                    file.delete();
                }
            }
        }
    }
}