package folk.sisby.inventory_tabs.providers;

public class UniqueItemTabProvider extends ItemTabProvider {
    @Override
    public int getRegistryPriority() {
        return 40;
    }

    @Override
    public boolean isUnique() {
        return true;
    }
}
