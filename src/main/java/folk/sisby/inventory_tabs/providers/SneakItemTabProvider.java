package folk.sisby.inventory_tabs.providers;

public class SneakItemTabProvider extends ItemTabProvider {
    @Override
    public int getRegistryPriority() {
        return 20;
    }

    @Override
    public boolean doSneakInteract() {
        return true;
    }
}
