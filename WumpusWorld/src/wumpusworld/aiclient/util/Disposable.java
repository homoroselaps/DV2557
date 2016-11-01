package wumpusworld.aiclient.util;


/**
 * Represents a component, that needs to be disposed when it is no longer needed.
 * <p>
 * Created by Nejc on 14. 10. 2016.
 */
public interface Disposable {


    /**
     * Releases or disposes all resources.
     */
    void dispose();


}
