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
import org.ocpsoft.rewrite.servlet.config.rule.Join;

@RewriteConfiguration
public class ZVotesConfigurationProvider extends HttpConfigurationProvider {

    @Override
    public int priority() {
        return 10;
    }

    @Override
    public Configuration getConfiguration(final ServletContext context) {
        return ConfigurationBuilder.begin()
                .addRule()
                .when(Direction.isInbound().and(Path.matches("/resources/{file}")).and(Query.matches("ln={type}")))
                .perform(Forward.to("/javax.faces.resource/{file}?ln={type}"))
                .addRule()
                .when(Direction.isOutbound().and(Path.matches("/javax.faces.resource/{file}")).and(Query.matches("ln={type}")))
                .perform(Substitute.with("/resources/{file}?ln={type}"))
                .addRule(Join.path("/").to("/index.xhtml"))
                .addRule(Join.path("/login/").to("/login.xhtml"))
                .addRule(Join.path("/account/").to("/account/index.xhtml"))
                .addRule(Join.path("/account/items/create/").to("/account/item_create.xhtml"))
                .addRule(Join.path("/account/items/edit/").to("/account/item_edit.xhtml"))
                .addRule(Join.path("/account/polls/").to("/account/poll_list.xhtml"))
                .addRule(Join.path("/account/polls/create/").to("/account/poll_create.xhtml"))
                .addRule(Join.path("/account/recipient-lists/").to("/account/recipientlist_list.xhtml"))
                .addRule(Join.path("/account/recipient-lists/create/").to("/account/recipientlist_create.xhtml"))
                .addRule(Join.path("/account/recipient-lists/edit/").to("/account/recipientlist_edit.xhtml"));
    }
}
