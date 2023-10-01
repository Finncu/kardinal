package org.kardinal.types.form;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.ActionComponent;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.interactions.components.ItemComponent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.modals.ModalMapping;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.jetbrains.annotations.NotNull;
import org.kardinal.types.base.InteractiveMessage;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Form extends InteractiveMessage {
    private final String name;
    private Color color;
    private final MessageCreateBuilder message = new MessageCreateBuilder();
    LinkedHashMap<String, String> fields = new LinkedHashMap<>();
    LinkedHashMap<String, ItemComponent> inputFields = new LinkedHashMap<>();

    public Form(String pName, @NotNull ActionComponent... pFields) {
        name = pName;
        for (ActionComponent field : pFields)
            setField(field);
    }

    public Form edit(ModalMapping... values) {
        for (ModalMapping mm : values)
            setValue(mm.getId(), mm.getAsString());
        return this;
    }

    private void setValue(String id, String value) {
        fields.put(id, value);
    }

    @Override
    public Form build() {
        message.clear().addActionRow(Button.secondary("edit", "Edit"));
        embed.setTitle(name);
        for (Map.Entry<String, String> field : fields.entrySet())
            embed.addField(field.getKey(), "``` " + field.getValue() + " ```", true);
        embed.setColor(color);
        return this;
    }

    private void setField(ActionComponent pField) {
            fields.put(pField.getId(), "");
            inputFields.put(pField.getId(), pField);
    }
}
