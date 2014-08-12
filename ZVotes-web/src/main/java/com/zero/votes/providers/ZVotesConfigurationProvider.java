package com.zero.votes.providers;

import javax.servlet.ServletContext;
import org.ocpsoft.rewrite.annotation.RewriteConfiguration;
import org.ocpsoft.rewrite.config.Configuration;
import org.ocpsoft.rewrite.config.ConfigurationBuilder;
import org.ocpsoft.rewrite.config.Direction;
import org.ocpsoft.rewrite.servlet.config.Forward;
import org.ocpsoft.rewrite.servlet.config.HttpConfigurationProvider;
import org.ocpsoft.rewrite.servlet.config.Path;
import org.ocpsoft.rewrite.servlet.config.Query;
import org.ocpsoft.rewrite.servlet.config.Substitute;
import org.ocpsoft.rewrite.servlet.config.rule.CDN;


@RewriteConfiguration
public class ZVotesConfigurationProvider extends HttpConfigurationProvider {
   @Override
   public int priority()
   {
     return 10;
   }

   @Override
   public Configuration getConfiguration(final ServletContext context)
   {
     return ConfigurationBuilder.begin()
            // Rewrite Rules for pretty resources
           .addRule()
           .when(Direction.isInbound().and(Path.matches("/resources/{file}")).and(Query.matches("ln={type}")))
           .perform(Forward.to("/faces/javax.faces.resource/{file}?ln={type}"))
           .addRule()
           .when(Direction.isOutbound().and(Path.matches("/faces/javax.faces.resource/{file}")).and(Query.matches("ln={type}")))
           .perform(Substitute.with("/resources/{file}?ln={type}"))
     ;
    }
}
