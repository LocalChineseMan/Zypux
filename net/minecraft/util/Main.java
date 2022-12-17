package net.minecraft.util;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

final class null extends Authenticator {
  protected PasswordAuthentication getPasswordAuthentication() {
    return new PasswordAuthentication(s1, s2.toCharArray());
  }
}
