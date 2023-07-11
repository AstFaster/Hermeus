package fr.astfaster.hermeus.api.util.builder;

import fr.astfaster.hermeus.api.HermeusException;
import fr.astfaster.hermeus.api.server.HermeusServer;

/**
 * An exception thrown by {@linkplain IBuilder builders} if there are invalid fields which stop object building.
 */
public class BuilderException extends HermeusException {

    public BuilderException(Class<? extends HermeusServer.Builder> builderClass, String... invalidFields) {
        super("Couldn't build " + builderClass.getName() + "! Invalid fields: " + formatFields(invalidFields) + ".");
    }

    private static String formatFields(String... fields) {
        final StringBuilder builder = new StringBuilder();

        for (String field : fields) {
            builder.append("'").append(field).append("', ");
        }

        final String formatted = builder.toString();

        return formatted.substring(0, formatted.length() - 2);
    }

}
