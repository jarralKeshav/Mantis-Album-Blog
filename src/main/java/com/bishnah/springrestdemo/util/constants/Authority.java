package com.bishnah.springrestdemo.util.constants;

import lombok.Locked;

public enum Authority {
    READ,
    WRITE,
    UPDATE,
    USER, // can update, delete self object and read anything
    ADMIN // update ,read, delete any object
}
