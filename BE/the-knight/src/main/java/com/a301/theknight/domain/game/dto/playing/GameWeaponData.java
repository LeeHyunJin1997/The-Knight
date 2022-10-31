package com.a301.theknight.domain.game.dto.playing;

import com.a301.theknight.domain.game.entity.Game;
import com.a301.theknight.domain.game.entity.Weapon;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class GameWeaponData implements Serializable {
    private int sword;
    private int twin;
    private int shield;
    private int hand;

    public static GameWeaponData toDto(Game game) {
        return GameWeaponData.builder()
                .sword(game.getSword())
                .shield(game.getShield())
                .twin(game.getTwin())
                .hand(game.getHand()).build();
    }

    public boolean canTakeWeapon(Weapon weapon) {
        if (Weapon.SWORD.equals(weapon) && sword > 0) {
            return true;
        } else if (Weapon.TWIN.equals(weapon) && twin > 0) {
            return true;
        } else if (Weapon.SHIELD.equals(weapon) && shield > 0) {
            return true;
        } else if (Weapon.HAND.equals(weapon) && hand > 0) {
            return true;
        }
        return false;
    }

    public void choiceWeapon(Weapon weapon) {
        if (Weapon.SWORD.equals(weapon)) {
            sword--;
        } else if (Weapon.TWIN.equals(weapon)) {
            twin--;
        } else if (Weapon.SHIELD.equals(weapon)) {
            shield--;
        } else if (Weapon.HAND.equals(weapon)) {
            hand--;
        }
    }

    public void returnWeapon(Weapon weapon) {
        if (Weapon.SWORD.equals(weapon)) {
            sword++;
        } else if (Weapon.TWIN.equals(weapon)) {
            twin++;
        } else if (Weapon.SHIELD.equals(weapon)) {
            shield++;
        } else if (Weapon.HAND.equals(weapon)) {
            hand++;
        }
    }
}