package com.sirma.pairofemployees.model.error;

public class NotFoundException extends  RuntimeException
{
    public NotFoundException(String msg)
    {
        super(msg);
    }
}