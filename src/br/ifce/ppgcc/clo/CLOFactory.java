package br.ifce.ppgcc.clo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.JSONArray;
import org.json.JSONObject;

import de.svenjacobs.loremipsum.LoremIpsum;

public class CLOFactory {
	
	private static final String DIRECTORY_NAME = "D:/clo";  
	
	private static final LoremIpsum LOREM_IPSUM = new LoremIpsum();
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final Random RANDOM = new Random();
	
	private static int cloId;
	
    public static void main(String[] args) throws Exception {
        
    	int qtdExecutaveis = Integer.parseInt(args[0]);
    	int qtdCenas = Integer.parseInt(args[1]);
    	int qtdComponentes = Integer.parseInt(args[2]);
    	int qtdArqMidias = Integer.parseInt(args[3]);
    	
    	System.out.println(new Date());
    	System.out.println("### Iniciando a geração do CLO com: ###");
    	System.out.println("-> " + qtdExecutaveis + " Executáveis");
    	System.out.println("-> " + qtdCenas + " Cenas");
    	System.out.println("-> " + qtdComponentes + " Componentes");
    	System.out.println("-> " + qtdArqMidias + " Arquivos de Mídia");
    	
    	cloId = gerarCLOId();
    	String cloTitle = "CLO " + cloId;
    	String cloFileName = cloTitle + ".zip";
    	String cloQualifiedName = "clo_" + cloId;
    	
    	System.out.println("### Nome CLO: " + cloFileName + " ###");
    	
    	ZipOutputStream clo = new ZipOutputStream(new FileOutputStream(DIRECTORY_NAME + "/" + cloFileName));
    	byte[] arrayBytes;
        int index;
    	
    	//Cria o LOM.json
    	JSONObject lomJson = gerarLOMJson(cloTitle, cloQualifiedName);
    	PrintWriter lomFile = new PrintWriter(DIRECTORY_NAME + "/temp/LOM.json", "UTF-8");
    	lomFile.write(lomJson.toString());
    	lomFile.close();
    	//Adiciona o LOM.json no CLO
    	FileInputStream fisLOMJson = new FileInputStream(DIRECTORY_NAME + "/temp/LOM.json");
    	clo.putNextEntry(new ZipEntry("LOM.json"));
    	arrayBytes = new byte[1024];
    	index = 0;    
        while ((index = fisLOMJson.read(arrayBytes)) > 0) {
            clo.write(arrayBytes, 0, index);
        }
        
        //Cria o diretório executable_files
        clo.putNextEntry(new ZipEntry("executable_files/"));
        
        File diretorioExecutaveisDisponiveis = new File(DIRECTORY_NAME + "/executaveis/");
        File[] executaveisDisponiveis = diretorioExecutaveisDisponiveis.listFiles();
        HashMap<Integer, String> executaveis = new HashMap<Integer, String>(); 
        
        for (int i = 0; i < qtdExecutaveis; i++) {        	
        	int indexExecutavel;
        	while (true) {
        		indexExecutavel = (int) (Math.random() * 5);
        		if (!executaveis.containsKey(indexExecutavel)) {
        			break;
        		}
        	}

        	//Obtém um Arquivo Executável aleatório
        	File executavel = executaveisDisponiveis[indexExecutavel];
        	String extensaoExecutavel = (executavel.getName().split("\\."))[1];
        	String diretorioExecutavelCLO = "executable_files/" + extensaoExecutavel + "/";
        	clo.putNextEntry(new ZipEntry(diretorioExecutavelCLO));
        	String caminhoExecutavel = diretorioExecutavelCLO + cloTitle + "." + extensaoExecutavel;
        	executaveis.put(indexExecutavel, caminhoExecutavel);
        	//Adiciona o Arquivo Executável no CLO
        	FileInputStream fisExecutavel = new FileInputStream(DIRECTORY_NAME + "/executaveis/" + executavel.getName());
        	clo.putNextEntry(new ZipEntry(caminhoExecutavel));
        	arrayBytes = new byte[1024];
        	index = 0;    
            while ((index = fisExecutavel.read(arrayBytes)) > 0) {
                clo.write(arrayBytes, 0, index);
            }
            
            //Cria o cloTitle_extensao.json
        	JSONObject lomExecutavelJson = gerarLOMExecutavelJson(executavel.getTotalSpace());
        	PrintWriter lomExecutavelFile = new PrintWriter(DIRECTORY_NAME + "/temp/" + cloTitle + "_" + extensaoExecutavel + ".json", "UTF-8");
        	lomExecutavelFile.write(lomExecutavelJson.toString());
        	lomExecutavelFile.close();
        	//Adiciona o cloTitle_extensao.json no CLO
        	FileInputStream fisLOMExecutavelJson = new FileInputStream(DIRECTORY_NAME + "/temp/" + cloTitle + "_" + extensaoExecutavel + ".json");
        	clo.putNextEntry(new ZipEntry(diretorioExecutavelCLO + cloTitle + "_" + extensaoExecutavel + ".json"));
        	arrayBytes = new byte[1024];
        	index = 0;    
            while ((index = fisLOMExecutavelJson.read(arrayBytes)) > 0) {
                clo.write(arrayBytes, 0, index);
            }
            
            //Cria o cloTitle.json            
        	JSONObject componentesJson = gerarComponentesJson(qtdCenas, qtdComponentes, qtdArqMidias);        	
            
            //Adiciona os Arquivos de Mídia em components
            for (int j = 0; j < qtdCenas; j++) {
            	JSONArray componentesCenaJson = (JSONArray) (((JSONObject) (componentesJson.getJSONArray("scenes").get(j))).getJSONArray("components"));
            	
            	for (int k = 0; k < componentesCenaJson.length(); k++) {
            		JSONObject componenteCenaJson = (JSONObject) componentesCenaJson.get(k);
            		
            		String name = componenteCenaJson.getString("name");
            		String newName = name + "_" + j + k;
        			componenteCenaJson.put("name", newName);
        			
            		if (componenteCenaJson.has("source")) {
            			String source = componenteCenaJson.getString("source");
            			FileInputStream fisArquivoMidia = new FileInputStream(DIRECTORY_NAME + "/arquivos_midia/" + source);
            			String newSource = "" + j + k + "_" + source;
            			componenteCenaJson.put("source", newSource);
            			clo.putNextEntry(new ZipEntry(diretorioExecutavelCLO + "components/" + newSource));
            			arrayBytes = new byte[1024];
                    	index = 0;    
                        while ((index = fisArquivoMidia.read(arrayBytes)) > 0) {
                            clo.write(arrayBytes, 0, index);
                        }
                        
                        //Finalizando a inclusão de um Arquivo de Mídia
                        fisArquivoMidia.close();
            		}
            	}
            }
            
            //Finaliza a criação do cloTitle.json no CLO
            PrintWriter componentesFile = new PrintWriter(DIRECTORY_NAME + "/temp/" + cloTitle + ".json", "UTF-8");
        	componentesFile.write(componentesJson.toString());
        	componentesFile.close();
        	//Adiciona o cloTitle.json no CLO
        	FileInputStream fisComponentesJson = new FileInputStream(DIRECTORY_NAME + "/temp/" + cloTitle + ".json");
        	clo.putNextEntry(new ZipEntry(diretorioExecutavelCLO + cloTitle + ".json"));
        	arrayBytes = new byte[1024];
        	index = 0;    
            while ((index = fisComponentesJson.read(arrayBytes)) > 0) {
                clo.write(arrayBytes, 0, index);
            }
            
        	//Finalizando a inclusão de um Executável
            fisExecutavel.close();
            fisLOMExecutavelJson.close();
            fisComponentesJson.close();
        }
        
        //Cria o MANIFEST.MF
    	String manifest = gerarManifest(executaveis.values());
    	PrintWriter manifestFile = new PrintWriter(DIRECTORY_NAME + "/temp/MANIFEST.MF", "UTF-8");
    	manifestFile.write(manifest);
    	manifestFile.close();
    	//Adiciona o MANIFEST.MF no CLO
    	FileInputStream fisManifest = new FileInputStream(DIRECTORY_NAME + "/temp/MANIFEST.MF");
    	clo.putNextEntry(new ZipEntry("MANIFEST.MF"));
    	arrayBytes = new byte[1024];
    	index = 0;    
        while ((index = fisManifest.read(arrayBytes)) > 0) {
            clo.write(arrayBytes, 0, index);
        }
                  
        //Finalizando
        clo.close();
        fisLOMJson.close();
        fisManifest.close();
        
        File diretorioTemporario = new File(DIRECTORY_NAME + "/temp/");
        limparDiretorio(diretorioTemporario);
        
        System.out.println(new Date());
    	System.out.println("### Finalizando a geração do CLO ###");
    }
    
    private static int gerarCLOId() {
    	return (int) (Math.random() * 1000);
    }
    
    private static JSONObject gerarLOMJson(String title, String qualified_name) {
    	JSONObject lomJson = new JSONObject();
    	
    	//Title
    	JSONObject titleJson = new JSONObject();
    	titleJson.put("language", LOREM_IPSUM.getWords(1));
    	titleJson.put("value", title);
    	lomJson.put("title", titleJson);
    	
    	//Qualified Name
    	lomJson.put("qualified_name", qualified_name);
    	
    	//Languages
    	JSONArray languagesJson = new JSONArray();
    	int qtdLanguages =  (int) (Math.random() * 10);
    	for (int i = 0; i < qtdLanguages; i++) {
    		languagesJson.put(LOREM_IPSUM.getWords(1));
    	}
    	lomJson.put("languages", languagesJson);
    	
    	//Descriptions
    	JSONArray descriptionsJson = new JSONArray();
    	int qtdDescriptions =  (int) (Math.random() * 10);
    	for (int i = 0; i < qtdDescriptions; i++) {
    		JSONObject descriptionJson = new JSONObject();
    		descriptionJson.put("language", LOREM_IPSUM.getWords(1));
    		descriptionJson.put("value", LOREM_IPSUM.getWords((int) (Math.random() * 100)));
    		descriptionsJson.put(descriptionJson);
    	}
    	lomJson.put("descriptions", descriptionsJson);
    	
    	//Keywords
    	JSONArray keywordsJson = new JSONArray();
    	int qtdKeywords =  (int) (Math.random() * 10);
    	for (int i = 0; i < qtdKeywords; i++) {
    		JSONObject keywordJson = new JSONObject();
    		keywordJson.put("language", LOREM_IPSUM.getWords(1));
    		keywordJson.put("value", LOREM_IPSUM.getWords((int) (Math.random() * 3)));
    		keywordsJson.put(keywordJson);
    	}
    	lomJson.put("keywords", keywordsJson);
    	
    	//Contributes
    	JSONArray contributesJson = new JSONArray();
    	int qtdContributes =  (int) (Math.random() * 10);
    	for (int i = 0; i < qtdContributes; i++) {
    		JSONObject contributeJson = new JSONObject();
    		JSONArray entitiesJson = (new JSONArray()).put(LOREM_IPSUM.getWords(3));
    		contributeJson.put("entity", entitiesJson);
    		contributeJson.put("date", DATE_FORMATTER.format(new Date()));
    		contributesJson.put(contributeJson);
    	}
    	lomJson.put("contributes", contributesJson);
    	
    	//Educational Informations
    	JSONArray educationalInformationsJson = new JSONArray();
    	int qtdEducationalInformations =  (int) (Math.random() * 10);
    	for (int i = 0; i < qtdEducationalInformations; i++) {
    		JSONObject educationalInformationJson = new JSONObject();
    		JSONObject interactivityTypeJson = ((new JSONObject()).put("source", LOREM_IPSUM.getWords(1))).put("value", LOREM_IPSUM.getWords(2));
    		educationalInformationJson.put("interactivity_type", interactivityTypeJson);
    		JSONObject interactivityLevelJson = ((new JSONObject()).put("source", LOREM_IPSUM.getWords(1))).put("value", LOREM_IPSUM.getWords(2));
    		educationalInformationJson.put("interactivity_level", interactivityLevelJson);
    		JSONArray typicalsAgeRangeJson = new JSONArray(); 
    		typicalsAgeRangeJson.put(((new JSONObject()).put("language", LOREM_IPSUM.getWords(1))).put("value", LOREM_IPSUM.getWords(1)));
    		educationalInformationJson.put("typicals_age_range", typicalsAgeRangeJson);
    		educationalInformationsJson.put(educationalInformationJson);
    	}
    	lomJson.put("educational_informations", educationalInformationsJson);
    	
    	//Cost
    	JSONObject costJson = new JSONObject();
    	costJson.put("source", LOREM_IPSUM.getWords(1));
    	costJson.put("value", LOREM_IPSUM.getWords(1));
    	lomJson.put("cost", costJson);
    	
    	//Copyright and Other Restrictions
    	JSONObject copyrightAndOtherRestrictionsJson = new JSONObject();
    	copyrightAndOtherRestrictionsJson.put("source", LOREM_IPSUM.getWords(1));
    	copyrightAndOtherRestrictionsJson.put("value", LOREM_IPSUM.getWords(1));
    	lomJson.put("copyright_and_other_restrictions", copyrightAndOtherRestrictionsJson);
    	
    	//Rights Description
    	JSONObject rightsDescriptionJson = new JSONObject();
    	rightsDescriptionJson.put("language", LOREM_IPSUM.getWords(1));
    	rightsDescriptionJson.put("value", LOREM_IPSUM.getWords((int) (Math.random() * 100)));
    	lomJson.put("rights_description", rightsDescriptionJson);
     	
    	//Annotations
    	JSONArray annotationsJson = new JSONArray();
    	int qtdAnnotations =  (int) (Math.random() * 10);
    	for (int i = 0; i < qtdAnnotations; i++) {
    		JSONObject annotationJson = new JSONObject();    	
    		annotationJson.put("entity", LOREM_IPSUM.getWords(3));
    		annotationJson.put("date", DATE_FORMATTER.format(new Date()));
    		JSONObject descriptionJson = ((new JSONObject()).put("language", LOREM_IPSUM.getWords(1))).put("value", LOREM_IPSUM.getWords((int) (Math.random() * 100)));
    		annotationJson.put("description", descriptionJson);
    		annotationsJson.put(annotationJson);
    	}
    	lomJson.put("annotations", annotationsJson);     	 
    	
    	return lomJson;
    }
    
    private static JSONObject gerarLOMExecutavelJson(long size) {
    	JSONObject lomExecutavelJson = new JSONObject();
    	
    	//Size
    	lomExecutavelJson.put("size", size);
    	
    	//Formats
    	JSONArray formatsJson = new JSONArray();
    	int qtdFormats =  (int) (Math.random() * 10);
    	for (int i = 0; i < qtdFormats; i++) {
    		formatsJson.put(LOREM_IPSUM.getWords(2));
    	}
    	lomExecutavelJson.put("formats", formatsJson);    	
    	
    	//Requirements
    	JSONArray requirementsJson = new JSONArray();
    	int qtdRequirements =  (int) (Math.random() * 10);
    	for (int i = 0; i < qtdRequirements; i++) {
    		JSONObject requirementJson = new JSONObject();
    		JSONObject typeJson = ((new JSONObject()).put("source", LOREM_IPSUM.getWords(1))).put("value", LOREM_IPSUM.getWords(3));
    		requirementJson.put("type", typeJson);
    		JSONObject nameJson = ((new JSONObject()).put("source", LOREM_IPSUM.getWords(1))).put("value", LOREM_IPSUM.getWords(3));
    		requirementJson.put("name", nameJson);
    		requirementJson.put("minimum_version", LOREM_IPSUM.getWords(1));
    		requirementJson.put("maximum_version", LOREM_IPSUM.getWords(1));
    		requirementsJson.put(requirementJson);
    	}
    	lomExecutavelJson.put("educational_informations", requirementsJson);
    	    	
    	//Installation Remark
    	JSONObject installationRemarkJson = new JSONObject();
    	installationRemarkJson.put("language", LOREM_IPSUM.getWords(1));
    	installationRemarkJson.put("value", LOREM_IPSUM.getWords((int) (Math.random() * 100)));
    	lomExecutavelJson.put("installation_remark", installationRemarkJson);     
    	
    	return lomExecutavelJson;
    }
    
    private static JSONObject gerarComponentesJson(int qtdCenas, int qtdComponentes, int qtdArqMidias) {
    	JSONObject componentesJson = new JSONObject();
    	JSONArray cenasJson = new JSONArray();
    	
    	int qtdComponentesPorCena = Math.floorDiv(qtdComponentes, qtdCenas);
    	int qtdComponentesSobrando = Math.floorMod(qtdComponentes, qtdCenas);
    	int qtdArqMidiasPorCena = Math.floorDiv(qtdArqMidias, qtdCenas);
    	int qtdArqMidiasSobrando = Math.floorMod(qtdArqMidias, qtdCenas);
 
    	for (int i = 1; i <= qtdCenas; i++) {
    		JSONObject cenaJson = new JSONObject();
    		
    		//Nome da Cena
    		cenaJson.put("scene", "cena_" + i);
    		
    		//Componentes da Cena
    		JSONArray componentesCenaJson = new JSONArray();
    		
    		int qtdComponentesCena;
    		int qtdArqMidiasCena;
    		if (i < qtdCenas) {
    			qtdComponentesCena = qtdComponentesPorCena;
    			qtdArqMidiasCena = qtdArqMidiasPorCena;
    		} else {
    			qtdComponentesCena = qtdComponentesPorCena + qtdComponentesSobrando;
    			qtdArqMidiasCena = qtdArqMidiasPorCena + qtdArqMidiasSobrando;
    		}
    		
    		int qtdArqMidiasAdicionadosCena = 0;
    		for (int j = 1; j <= qtdComponentesCena; j++) {
    			JSONObject componenteJson = new JSONObject();
    			TiposComponente tipoComponente;
    			
    			int indexTipoComponente;
    			if (qtdArqMidiasAdicionadosCena < qtdArqMidiasCena) {
    				indexTipoComponente = RANDOM.nextInt(
    						(TiposComponente.VIDEO.ordinal() - TiposComponente.IMAGEM.ordinal()) + 1) + TiposComponente.IMAGEM.ordinal();    				
    				qtdArqMidiasAdicionadosCena++;
    			} else {
    				indexTipoComponente = RANDOM.nextInt(
    						(TiposComponente.BOTAO.ordinal() - TiposComponente.TEXTO.ordinal()) + 1) + TiposComponente.TEXTO.ordinal();
    			}

    			tipoComponente = TiposComponente.values()[indexTipoComponente]; 
    			File diretorioComponentesDisponiveis = new File(DIRECTORY_NAME + "/arquivos_midia/");
    			File[] arquivosMidia;
    			for (String atributo : tipoComponente.getAtributos()) {    				
    				switch (atributo) {
    				case "name":
						componenteJson.put("name", "componente");
						break;
					case "enabled":
						componenteJson.put("enabled", (Math.random() < 0.5));
						break;
					case "visibled":
						componenteJson.put("visibled", (Math.random() < 0.5));
						break;	
					case "text":
						componenteJson.put("text", LOREM_IPSUM.getWords((int) (Math.random() * 100)));
						break;	
					case "position":
						StringBuilder position = new StringBuilder();
						position.append("D:");
						position.append(((int) (Math.random() * 500))).append(":");
						position.append(((int) (Math.random() * 500))).append(":");
						position.append(((int) (Math.random() * 500))).append(":");
						position.append(((int) (Math.random() * 500))).append(":");
						position.append(((int) (Math.random() * 500))).append(":");
						position.append(((int) (Math.random() * 500)));
						componenteJson.put("position", position.toString());
						break;
					case "zindex":
						componenteJson.put("zindex", ((int) (Math.random() * 10)));
						break;
					case "label":
						componenteJson.put("label", LOREM_IPSUM.getWords((int) (Math.random() * 3)));
						break;
					case "startTime":
						componenteJson.put("startTime", ((int) (Math.random() * 500)));
						break;
					case "stopTime":
						componenteJson.put("stopTime", ((int) (Math.random() * 500)));
						break;
					case "source":
						arquivosMidia = diretorioComponentesDisponiveis.listFiles(new FilenameFilter() {
						    public boolean accept(File dir, String name) {
						        return name.startsWith(tipoComponente.toString());
						    }
						});
						componenteJson.put("source", arquivosMidia[0].getName());						
						break;
					default:
						break;
					}
    			}
    			
    			componentesCenaJson.put(componenteJson);
    		}
    		
    		cenaJson.put("components", componentesCenaJson);
    		
    		cenasJson.put(cenaJson);
    	}
        
    	componentesJson.put("scenes", cenasJson);
    	
    	return componentesJson;
    }
    
    private static String gerarManifest(Collection<String> caminhoExecutaveisCLO) {
    	StringBuilder manifest = new StringBuilder();
    	
    	manifest.append("Manifest-Version: 1.0").append("\n");
    	for (String caminhoExecutavelCLO : caminhoExecutaveisCLO) {
    		String[] tokensCaminhoExecutavel = caminhoExecutavelCLO.split("/");
    		manifest.append("Executable-File-Directory: ").append(tokensCaminhoExecutavel[0]).append("/").append(tokensCaminhoExecutavel[1]).append("\n");
    		manifest.append("Executable-File: ").append(tokensCaminhoExecutavel[2]).append("\n");
    	}
    	
    	return manifest.toString();
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