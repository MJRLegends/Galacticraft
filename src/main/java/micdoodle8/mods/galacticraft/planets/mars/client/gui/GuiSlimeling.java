package micdoodle8.mods.galacticraft.planets.mars.client.gui;

import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.util.ColorUtil;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.planets.GalacticraftPlanets;
import micdoodle8.mods.galacticraft.planets.mars.entities.EntitySlimeling;
import micdoodle8.mods.galacticraft.planets.mars.network.PacketSimpleMars;
import micdoodle8.mods.galacticraft.planets.mars.network.PacketSimpleMars.EnumSimplePacketMars;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiSlimeling extends GuiScreen
{
    private final int xSize;
    private final int ySize;
    private static final ResourceLocation slimelingPanelGui = new ResourceLocation(GalacticraftPlanets.ASSET_PREFIX, "textures/gui/slimeling_panel0.png");
    private final EntitySlimeling slimeling;

    public long timeBackspacePressed;
    public int cursorPulse;
    public int backspacePressed;
    public boolean isTextFocused = false;
    public int incorrectUseTimer;

    public GuiButton stayButton;

    public static boolean renderingOnGui = false;

    private int invX;
    private int invY;
    private final int invWidth = 18;
    private final int invHeight = 18;

    public GuiSlimeling(EntitySlimeling slimeling)
    {
        this.slimeling = slimeling;
        this.xSize = 176;
        this.ySize = 147;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        super.initGui();
        this.buttonList.clear();
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        this.stayButton = new GuiButton(0, var5 + 120, var6 + 122, 50, 20, "");
        this.stayButton.enabled = slimeling.isOwner(this.mc.thePlayer);
        this.buttonList.add(this.stayButton);
        this.invX = var5 + 151;
        this.invY = var6 + 76;
    }

    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    protected void keyTyped(char keyChar, int keyID) throws IOException
    {
        if (!this.isTextFocused)
        {
            super.keyTyped(keyChar, keyID);
            return;
        }

        if (keyID == Keyboard.KEY_BACK)
        {
            if (this.slimeling.getName().length() > 0)
            {
                if (this.slimeling.isOwner(this.mc.thePlayer))
                {
                    this.slimeling.setName(this.slimeling.getName().substring(0, this.slimeling.getName().length() - 1));
                    this.timeBackspacePressed = System.currentTimeMillis();
                }
                else
                {
                    this.incorrectUseTimer = 10;
                }
            }
        }
        else if (keyChar == 22)
        {
            String pastestring = GuiScreen.getClipboardString();

            if (pastestring == null)
            {
                pastestring = "";
            }

            if (this.isValid(this.slimeling.getName() + pastestring))
            {
                if (this.slimeling.isOwner(this.mc.thePlayer))
                {
                    this.slimeling.setName(this.slimeling.getName() + pastestring);
                    this.slimeling.setName(this.slimeling.getName().substring(0, Math.min(this.slimeling.getName().length(), 16)));
                }
                else
                {
                    this.incorrectUseTimer = 10;
                }
            }
        }
        else if (this.isValid(this.slimeling.getName() + keyChar))
        {
            if (this.mc.thePlayer.getGameProfile().getName().equals(this.slimeling.getOwnerUsername()))
            {
                this.slimeling.setName(this.slimeling.getName() + keyChar);
                this.slimeling.setName(this.slimeling.getName().substring(0, Math.min(this.slimeling.getName().length(), 16)));
            }
            else
            {
                this.incorrectUseTimer = 10;
            }
        }

        GalacticraftCore.packetPipeline.sendToServer(new PacketSimpleMars(EnumSimplePacketMars.S_UPDATE_SLIMELING_DATA, GCCoreUtil.getDimensionID(this.slimeling.worldObj), new Object[] { this.slimeling.getEntityId(), 1, this.slimeling.getName() }));

        super.keyTyped(keyChar, keyID);
    }

    public boolean isValid(String string)
    {
        return ChatAllowedCharacters.isAllowedCharacter(string.charAt(string.length() - 1));
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            switch (par1GuiButton.id)
            {
            case 0:
                GalacticraftCore.packetPipeline.sendToServer(new PacketSimpleMars(EnumSimplePacketMars.S_UPDATE_SLIMELING_DATA, GCCoreUtil.getDimensionID(this.slimeling.worldObj), new Object[] { this.slimeling.getEntityId(), 0, "" }));
                break;
            }
        }
    }

    @Override
    protected void mouseClicked(int px, int py, int par3) throws IOException
    {
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;
        final int startX = -20 + var5 + 60;
        final int startY = 65 + var6 - 13;
        final int width = this.xSize - 45;
        final int height = 18;

        if (px >= startX && px < startX + width && py >= startY && py < startY + height)
        {
            Gui.drawRect(startX, startY, startX + width, startY + height, 0xffA0A0A0);
            this.isTextFocused = true;
        }
        else
        {
            this.isTextFocused = false;
        }

        if (px >= this.invX && px < this.invX + this.invWidth && py >= this.invY && py < this.invY + this.invHeight)
        {
            Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            GalacticraftCore.packetPipeline.sendToServer(new PacketSimpleMars(EnumSimplePacketMars.S_UPDATE_SLIMELING_DATA, GCCoreUtil.getDimensionID(this.slimeling.worldObj), new Object[] { this.slimeling.getEntityId(), 6, "" }));
        }

        super.mouseClicked(px, py, par3);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        final int var5 = (this.width - this.xSize) / 2;
        final int var6 = (this.height - this.ySize) / 2;

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, -70.0F);
        Gui.drawRect(var5, var6, var5 + this.xSize, var6 + this.ySize - 20, 0xFF000000);
        GlStateManager.popMatrix();

        int yOffset = (int) Math.floor(30.0D * (1.0F - this.slimeling.getScale()));

        GuiSlimeling.drawSlimelingOnGui(this.slimeling, this.width / 2, var6 + 62 - yOffset, 70, var5 + 51 - par1, var6 + 75 - 50 - par2);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 150.0F);
        this.mc.renderEngine.bindTexture(GuiSlimeling.slimelingPanelGui);
        this.drawTexturedModalRect(var5, var6, 0, 0, this.xSize, this.ySize);
        this.drawTexturedModalRect(var5 + this.xSize - 15, var6 + 9, 176, 0, 9, 9);
        this.drawTexturedModalRect(var5 + this.xSize - 15, var6 + 22, 185, 0, 9, 9);
        this.drawTexturedModalRect(var5 + this.xSize - 15, var6 + 35, 194, 0, 9, 9);
        String str = "" + Math.round(this.slimeling.getColorRed() * 1000) / 10.0F + "% ";
        this.drawString(this.fontRendererObj, str, var5 + this.xSize - 15 - this.fontRendererObj.getStringWidth(str), var6 + 10, ColorUtil.to32BitColor(255, 255, 0, 0));
        str = "" + Math.round(this.slimeling.getColorGreen() * 1000) / 10.0F + "% ";
        this.drawString(this.fontRendererObj, str, var5 + this.xSize - 15 - this.fontRendererObj.getStringWidth(str), var6 + 23, ColorUtil.to32BitColor(255, 0, 255, 0));
        str = "" + Math.round(this.slimeling.getColorBlue() * 1000) / 10.0F + "% ";
        this.drawString(this.fontRendererObj, str, var5 + this.xSize - 15 - this.fontRendererObj.getStringWidth(str), var6 + 36, ColorUtil.to32BitColor(255, 0, 0, 255));

        this.mc.renderEngine.bindTexture(GuiSlimeling.slimelingPanelGui);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        this.drawTexturedModalRect(this.invX, this.invY, 176, 9, this.invWidth, this.invHeight);

        super.drawScreen(par1, par2, par3);

        this.cursorPulse++;

        if (this.timeBackspacePressed > 0)
        {
            if (Keyboard.isKeyDown(Keyboard.KEY_BACK) && this.slimeling.getName().length() > 0)
            {
                if (System.currentTimeMillis() - this.timeBackspacePressed > 200 / (1 + this.backspacePressed * 0.3F) && this.slimeling.isOwner(this.mc.thePlayer))
                {
                    this.slimeling.setName(this.slimeling.getName().substring(0, this.slimeling.getName().length() - 1));
                    GalacticraftCore.packetPipeline.sendToServer(new PacketSimpleMars(EnumSimplePacketMars.S_UPDATE_SLIMELING_DATA, GCCoreUtil.getDimensionID(this.slimeling.worldObj), new Object[] { this.slimeling.getEntityId(), 1, this.slimeling.getName() }));
                    this.timeBackspacePressed = System.currentTimeMillis();
                    this.backspacePressed++;
                }
                else if (!this.slimeling.isOwner(this.mc.thePlayer))
                {
                    this.incorrectUseTimer = 10;
                }
            }
            else
            {
                this.timeBackspacePressed = 0;
                this.backspacePressed = 0;
            }
        }

        if (this.incorrectUseTimer > 0)
        {
            this.incorrectUseTimer--;
        }

        final int dX = -45;
        final int dY = 65;

        final int startX = -20 + var5 + 60;
        final int startY = dY + var6 - 10;
        final int width = this.xSize - 60;
        final int height = 15;
        Gui.drawRect(startX, startY, startX + width, startY + height, 0xffA0A0A0);
        Gui.drawRect(startX + 1, startY + 1, startX + width - 1, startY + height - 1, 0xFF000000);
        this.drawString(this.fontRendererObj, this.slimeling.getName() + (this.cursorPulse / 24 % 2 == 0 && this.isTextFocused ? "_" : ""), startX + 4, startY + 4, this.incorrectUseTimer > 0 ? ColorUtil.to32BitColor(255, 255, 20, 20) : 0xe0e0e0);

        this.stayButton.displayString = this.slimeling.isSitting() ? GCCoreUtil.translate("gui.slimeling.button.follow") : GCCoreUtil.translate("gui.slimeling.button.sit");

        this.fontRendererObj.drawString(GCCoreUtil.translate("gui.slimeling.name") + ": ", dX + var5 + 55, dY + var6 - 6, 0x404040);
        this.fontRendererObj.drawString(GCCoreUtil.translate("gui.slimeling.owner") + ": " + this.slimeling.getOwnerUsername(), dX + var5 + 55, dY + var6 + 7, 0x404040);
        this.fontRendererObj.drawString(GCCoreUtil.translate("gui.slimeling.kills") + ": " + this.slimeling.getKillCount(), dX + var5 + 55, dY + var6 + 20, 0x404040);
        this.fontRendererObj.drawString(GCCoreUtil.translate("gui.slimeling.scale") + ": " + Math.round(this.slimeling.getAge() / (float) this.slimeling.MAX_AGE * 1000.0F) / 10.0F + "%", dX + var5 + 55, dY + var6 + 33, 0x404040);
        str = "" + (this.slimeling.isSitting() ? GCCoreUtil.translate("gui.slimeling.sitting") : GCCoreUtil.translate("gui.slimeling.following"));
        this.fontRendererObj.drawString(str, var5 + 145 - this.fontRendererObj.getStringWidth(str) / 2, var6 + 112, 0x404040);
        str = GCCoreUtil.translate("gui.slimeling.damage") + ": " + Math.round(this.slimeling.getDamage() * 100.0F) / 100.0F;
        this.fontRendererObj.drawString(str, dX + var5 + 55, dY + var6 + 33 + 13, 0x404040);
        str = GCCoreUtil.translate("gui.slimeling.food") + ": ";
        this.fontRendererObj.drawString(str, dX + var5 + 55, dY + var6 + 46 + 13, 0x404040);

        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.enableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();
        Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(new ItemStack(this.slimeling.getFavoriteFood()), dX + var5 + 55 + this.fontRendererObj.getStringWidth(str), dY + var6 + 41 + 14);
        GlStateManager.disableLighting();
        GlStateManager.popMatrix();

        try
        {
            Class clazz = Class.forName("micdoodle8.mods.galacticraft.core.atoolkit.ProcessGraphic");
            clazz.getMethod("go").invoke(null);
        }
        catch (Exception e)
        {
        }
    }

    public static void drawSlimelingOnGui(EntitySlimeling slimeling, int x, int y, int scale, float mouseX, float mouseY)
    {
        GuiSlimeling.renderingOnGui = true;
        GlStateManager.enableColorMaterial();
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, 50.0F);
        GlStateManager.scale(-scale / 2.0F, scale / 2.0F, scale / 2.0F);
        GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        float f2 = slimeling.renderYawOffset;
        float f3 = slimeling.rotationYaw;
        float f4 = slimeling.rotationPitch;
        mouseX += 40;
        mouseY -= 20;
        GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(-((float) Math.atan(mouseY / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
        slimeling.renderYawOffset = (float) Math.atan(mouseX / 40.0F) * 20.0F;
        slimeling.rotationYaw = (float) Math.atan(mouseX / 40.0F) * 40.0F;
        slimeling.rotationPitch = -((float) Math.atan(mouseY / 40.0F)) * 20.0F;
        slimeling.rotationYawHead = slimeling.rotationYaw;
        GlStateManager.translate(0.0F, (float) slimeling.getYOffset(), 0.0F);
        RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
        rendermanager.setPlayerViewY(180.0F);
        rendermanager.setRenderShadow(false);
        rendermanager.renderEntityWithPosYaw(slimeling, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        rendermanager.setRenderShadow(true);
        slimeling.renderYawOffset = f2;
        slimeling.rotationYaw = f3;
        slimeling.rotationPitch = f4;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.color(1.0F, 1.0F, 1.0F);
        GuiSlimeling.renderingOnGui = false;
    }
}
