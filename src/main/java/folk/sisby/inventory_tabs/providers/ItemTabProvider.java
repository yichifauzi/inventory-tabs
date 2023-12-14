package folk.sisby.inventory_tabs.providers;

import folk.sisby.inventory_tabs.tabs.ItemTab;
import folk.sisby.inventory_tabs.tabs.Tab;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class ItemTabProvider extends RegistryTabProvider<Item> {
    public final Map<Identifier, Predicate<ItemStack>> preclusions = new HashMap<>();

    @Override
    public void addAvailableTabs(ClientPlayerEntity player, Consumer<Tab> addTab) {
        Set<Item> itemsAdded = new HashSet<>();
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (values.contains(stack.getItem()) && preclusions.values().stream().noneMatch(p -> p.test(stack))) {
                if (isUnique() && !itemsAdded.add(stack.getItem())) continue;
                addTab.accept(createTab(stack, i));
            }
        }
    }

    public Tab createTab(ItemStack stack, int slot) {
        return new ItemTab(stack, slot, preclusions, isUnique());
    }
}
