package Apec.Components.Gui.Menu.CustomizationMenu;

import Apec.ApecMain;
import Apec.ApecUtils;
import Apec.Components.Gui.GuiIngame.GuiElements.GUIComponent;
import Apec.Settings.SettingID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class CustomizationGuiButton extends GuiButton {

    Vector2f initialPos;

    List<Integer> xSnapPoint, ySnapPoints;
    GUIComponent guiComponent;
    public boolean isUserDragging = false;
    private Vector2f fineTuned = new Vector2f(0,0);

    public CustomizationGuiButton (int x,int y,int w,int h,GUIComponent component,List<Integer> xSnapPoint, List<Integer> ySnapPoints) {
        super(0,x,y,w,h,"");
        this.guiComponent = component;
        this.xSnapPoint = xSnapPoint;
        this.ySnapPoints = ySnapPoints;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible)
        {
            ScaledResolution sr = new ScaledResolution(mc);

            Vector2f v = ApecUtils.addVec(this.guiComponent.getAnchorPointPosition(new ScaledResolution(mc)),this.guiComponent.getDelta_position());
            this.xPosition = (int)v.x - width/2;
            this.yPosition = (int)v.y - height/2;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            if (this.hovered) drawRect(this.xPosition,this.yPosition,this.xPosition+this.width,this.yPosition+this.height,0x3adddddd);
            else drawRect(this.xPosition,this.yPosition,this.xPosition+this.width,this.yPosition+this.height,0x6adddddd);
            drawRect(xPosition,yPosition - 1,xPosition + width,yPosition,0xffffffff);
            drawRect(xPosition+width,yPosition,xPosition+width+1,yPosition+height,0xffffffff);
            drawRect(xPosition,yPosition+height,xPosition+width,yPosition+height+1,0xffffffff);
            drawRect(xPosition-1,yPosition,xPosition,yPosition+height,0xffffffff);
            this.mouseDragged(mc, mouseX, mouseY);

            if (isUserDragging) {
                /*if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) {
                    float magnitudeOfDifference = ApecUtils.getMagnitude(initialPos,new Vector2f(mouseX,mouseY)) - 30;
                    magnitudeOfDifference /= 100;
                    this.guiComponent.setScale(magnitudeOfDifference);
                    return;
                }*/

                Vector2f anchor = this.guiComponent.getAnchorPointPosition(new ScaledResolution(mc));

                boolean isSnappedToPositionX = false;
                boolean isSnappedToPositionY = false;

                if (ApecMain.Instance.settingsManager.getSettingState(SettingID.SNAP_IN_EDITING)) {
                    for (Integer i : xSnapPoint) {
                        if (Math.abs(i - mouseX) < 5) {
                            isSnappedToPositionX = true;
                            this.guiComponent.setDelta_position(new Vector2f(i - anchor.x, mouseY - anchor.y));
                            drawRect(i, 0, i + 1, sr.getScaledHeight(), 0xffff0000);
                            break;
                        }
                    }

                    for (Integer i : ySnapPoints) {
                        if (Math.abs(i - mouseY) < 5) {
                            isSnappedToPositionY = true;
                            this.guiComponent.setDelta_position(new Vector2f(isSnappedToPositionX ? this.guiComponent.getDelta_position().x : mouseX - anchor.x, i - anchor.y));
                            drawRect(0, i, sr.getScaledWidth(), i + 1, 0xff0000ff);
                            break;
                        }
                    }
                }

                this.guiComponent.setDelta_position(new Vector2f(
                        isSnappedToPositionX ? this.guiComponent.getDelta_position().x +fineTuned.x : mouseX - anchor.x + fineTuned.x,
                        isSnappedToPositionY ? this.guiComponent.getDelta_position().y + fineTuned.y: mouseY - anchor.y + fineTuned.y
                ));
            } else {
                this.fineTuned= new Vector2f(0,0);
            }
        }
    }

    public void userStartedDragging(int mouseX,int mouseY) {
        initialPos = new Vector2f(mouseX,mouseY);
        this.isUserDragging = true;
    }

    public void setDeltaToZero() {
        this.guiComponent.setDelta_position(new Vector2f(0,0));
    }

    public void fineRepositioning(Vector2f v) {
        if (this.isUserDragging) this.fineTuned = ApecUtils.addVec(fineTuned,v);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        super.mouseReleased(mouseX, mouseY);
        this.isUserDragging = false;
    }
}