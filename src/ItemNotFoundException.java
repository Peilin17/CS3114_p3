/**
 * Exception class for failed finds/removes in search
 * trees and iterators.
 * 
 * @author Naod Haregot
 * @version 2019.02.09
 *
 */
public class ItemNotFoundException 
    extends RuntimeException
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Constructs this exception object.
     */
    public ItemNotFoundException()
    {
        super();
    }


    // ----------------------------------------------------------
    /**
     * Constructs this exception object.
     * @param message the error message.
     */
    public ItemNotFoundException(String message)
    {
        super(message);
    }
}
