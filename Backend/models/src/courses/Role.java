/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package courses;

/**
 *
 * @author jorsi
 */

public enum Role {
    NONE(0), ADMIN(1), TEACHER(2), STUDENT(3);

    private final int value;
    private Role(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

