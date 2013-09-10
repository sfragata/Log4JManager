/**
 * 
 */
package br.com.sfragata.log4jmanager;

import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Component;

/**
 * Classe que será exportada por JMX para gerenciar o LEVELS do Log4J
 * configurados na aplicação
 * 
 * Exported JMX class to manager the Log4J's levels and categories
 * 
 * Classe JMX exportée pour la configuration des niveaux et catégories
 * 
 * @author Fragata da Silva, Silvio
 */
@Component
@ManagedResource(objectName = "br.com.sfragata:name=log4jManager", description = "Exported JMX class to manager the Log4J's levels and categories", currencyTimeLimit = 15, persistPolicy = "OnUpdate", persistPeriod = 200, persistLocation = "log4j", persistName = "log4jManager")
public class Logger4jManager {

	private static final Log logger = LogFactory.getLog(Logger4jManager.class);

	private Logger getLog(String category) {
		try {
			Logger l = LogManager.getLogger(category);
			if (logger.isDebugEnabled()) {
				logger.debug("Log: " + l.getName());
			}
			return l;
		} catch (Exception e) {
			logger.error(
					new StringBuilder("Category not found: ").append(category),
					e);
			return null;
		}
	}

	/**
	 * Retrieve the Log's level
	 * 
	 * @param category
	 *            Category's name
	 * @return The Level or "" if there isn't the given category
	 */
	@ManagedOperation(description = "Retrieve the Log's level")
	@ManagedOperationParameters({ @ManagedOperationParameter(name = "category", description = "Category's name") })
	public String getLevel(String category) {
		Logger log = getLog(category);
		String level = "";
		if (log != null) {
			try {
				level = log.getLevel().toString();
			} catch (Exception e) {
				return "Wrong category " + category;
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Level of category " + category + " = " + level);
		}

		return level;
	}

	/**
	 * Update the Log's level
	 * 
	 * @param category
	 *            Category's name
	 * @param level
	 *            Level name
	 */
	@ManagedOperation(description = "Update the Log's level")
	@ManagedOperationParameters({
			@ManagedOperationParameter(name = "category", description = "Category's name"),
			@ManagedOperationParameter(name = "level", description = "Level name") })
	public void setLevel(String category, String level) {
		Logger log = getLog(category);
		if (log != null && level != null) {
			log.setLevel(Level.toLevel(level.toUpperCase()));
		}
	}

	/**
	 * Retrieve all categories and their levels
	 * 
	 * @return a String with all categories and their levels
	 */
	@ManagedOperation(description = "Retrieve all categories and their levels")
	public String retrieveLoggers() {
		StringBuilder logs = new StringBuilder();
		Enumeration<?> en = LogManager.getCurrentLoggers();
		while (en.hasMoreElements()) {
			Logger log = (Logger) en.nextElement();
			Level level = log.getLevel();
			if (level != null) {
				logs.append(log.getName()).append(" [")
						.append(level.toString()).append("]").append("\n");
			}
		}
		return logs.toString();
	}
}
