package net.werdei.serverhats;

import net.fabricmc.fabric.api.item.v1.EquipmentSlotProvider;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class HeadEquipmentSlotProvider implements EquipmentSlotProvider
{
    public static HeadEquipmentSlotProvider PROVIDER = new HeadEquipmentSlotProvider();

    private HeadEquipmentSlotProvider() {} //To not create a new one accidentally

    @Override
    public EquipmentSlot getPreferredEquipmentSlot(ItemStack stack)
    {
        return EquipmentSlot.HEAD;
    }
}
