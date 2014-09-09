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
                .addRule(Join.path("/poll/").to("/poll.xhtml"))
                .addRule(Join.path("/result/{poll}").to("/result.xhtml?poll={poll}"))
                .addRule(Join.path("/token/").to("/token.xhtml"))
                .addRule(Join.path("/token/{token}").to("/token.xhtml?token={token}"))

                .addRule(Join.path("/account/").to("/account/index.xhtml"))
                .addRule(Join.path("/account/items/").to("/account/item_list.xhtml"))
                .addRule(Join.path("/account/item/create/").to("/account/item_create.xhtml"))
                .addRule(Join.path("/account/item/edit/").to("/account/item_edit.xhtml"))
                .addRule(Join.path("/account/item-option/create/").to("/account/item_option_create.xhtml"))
                .addRule(Join.path("/account/item-option/edit/").to("/account/item_option_edit.xhtml"))
                .addRule(Join.path("/account/organizers/").to("/account/organizer_list.xhtml"))
                .addRule(Join.path("/account/organizer/add/").to("/account/organizer_add.xhtml"))
                .addRule(Join.path("/account/participants/").to("/account/participant_list.xhtml"))
                .addRule(Join.path("/account/participant/create/").to("/account/participant_create.xhtml"))
                .addRule(Join.path("/account/participant/edit/").to("/account/participant_edit.xhtml"))
                .addRule(Join.path("/account/participant/import-list/").to("/account/participant_import_list.xhtml"))
                .addRule(Join.path("/account/polls/").to("/account/poll_list.xhtml"))
                .addRule(Join.path("/account/poll/create/").to("/account/poll_create.xhtml"))
                .addRule(Join.path("/account/poll/edit/").to("/account/poll_edit.xhtml"))
                .addRule(Join.path("/account/recipients/").to("/account/recipient_list.xhtml"))
                .addRule(Join.path("/account/recipient/create/").to("/account/recipient_create.xhtml"))
                .addRule(Join.path("/account/recipient/edit/").to("/account/recipient_edit.xhtml"))
                .addRule(Join.path("/account/recipient-lists/").to("/account/recipientlist_list.xhtml"))
                .addRule(Join.path("/account/recipient-list/create/").to("/account/recipientlist_create.xhtml"))
                .addRule(Join.path("/account/recipient-list/edit/").to("/account/recipientlist_edit.xhtml"))

                .addRule(Join.path("/admin/").to("/admin/list.xhtml"))
                .addRule(Join.path("/admin/admins/").to("/admin/admin_list.xhtml"))
                .addRule(Join.path("/admin/admins/add/").to("/admin/admin_add.xhtml"))
                .addRule(Join.path("/admin/poll/").to("/admin/poll_list.xhtml"));
    }
}
