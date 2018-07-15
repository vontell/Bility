package io.github.ama_csail.ama.util;

/**
 * An enumerated type which indicates that a certain view has a certain "connotation", such
 * as a dangerous action.
 * @author Aaron Vontell
 */
public enum ActionClass {
    DEFAULT, PRIMARY, SUCCESS, INFO, WARNING, DANGER, LINK, UNSET
}
