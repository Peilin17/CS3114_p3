/**
 * Exception class for duplicate item errors
 * in search tree insertions.
 * 
 * @author Naod Haregot (nharegot)
 * @version 2019.02.09
 */
public class DuplicateItemException 
    extends RuntimeException
{
    //~ Constructors ..........................................................

    // ----------------------------------------------------------
    /**
     * Constructs this exception object.
     */
    public DuplicateItemException()
    {
        super();
    }


    // ----------------------------------------------------------
    /**
     * Constructs this exception object.
     * @param message the error message.
     */
    public DuplicateItemException(String message)
    {
        super(message);
    }
}
