======================
Alfresco Language Pack
======================

Version : 1.2
Idioma : es_ES

===========================
Contenido del Language Pack
===========================

*_es_ES.properties

============
Instalación
============

Explicación original de Alfresco en la siguiente URL:
http://wiki.alfresco.com/wiki/Language_Pack_Installation

- Copiar todos los ficheros en <custom config>/messages. 
	A partir de la versión 1.2, la carpeta config puede estar en una localización diferente al raíz de la aplicación web.
  
  Localizaciones de Configuraciones personalizadas
    * Tomcat - <Tomcat home>/shared/classes/alfresco (por ejemplo, C:\alfresco\tomcat\shared\classes\alfresco)
    * JBoss - <JBoss home>/server/default/conf (por ejemplo, C:\alfresco\jboss\server\default\conf) 
    
- Opciones de Idioma del Login
Para cambiar las opciones de Idioma en la pantalla de login, tendrás que modificar el fichero web-client-config-custom ( puede que tengas que renombrar el fichero de ejemplo ):

    * Editar el fichero 'web-client-config-custom.xml' en la carpeta <custom config>/extension
    * Busca la seccion '<languages>'
    * Añade o quita entradas de idioma que aparecen de la siguiente manera '<language locale="XX_YY">LangName</language>'
    - Añadir la siguiente entrada para que el idioma español esté disponible :
       '<language locale="es_ES">Espa&#241;ol (Espa&#241;a)</language>'
    * Guarda el fichero    
    
 <!--
   <config evaluator="string-compare" condition="Languages">
     <languages>
     		<language locale="es_ES">Espa&#241;ol (Espa&#241;a)</language>
        <language locale="fr_FR">French</language>
        <language locale="de_DE">German</language>
        <language locale="ja_JP">Japanese</language>
     </languages>
   </config>
  -->


- Reiniciar Alfresco

================================
Contribuyentes del Spanish Language Pack
================================
Autores Originales: El equipo de Alfresco
Contribuyentes:
Traductores Inglés - Español : El equipo de Evoltia (Alfresco Gold partner)http://www.evoltia.com
Gracias también a los traductores de las versiones anteriores. 