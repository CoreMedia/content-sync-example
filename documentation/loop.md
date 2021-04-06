# List of Open Points

--------------------------------------------------------------------------------

\[[Up](README.md)\] \[[Top](#top)\]

--------------------------------------------------------------------------------

* Access to the synchronization app is only possible for members of group _Administratoren_.
* All running synchronization workflows are reported in the corresponding Studio app panel with the very same name.  
* Settings for _Excluded Properties_ and _Excluded Content Types_ are only evaluated in Studio frontend, not in the workflow itself. 
* Property _masterVersion_ of all content types is ignored during synchronization since it is a more complex task to update it according to user's intention. Localization workflows may thus break or behave unexpectedly due to missing or not updated master versions.
* JWT token for access to the Ingest Service is short-lived and must be renewed every **TODO** days which implies a change of Studio properties and thus a deployment request. Storing the token in content settings (and making it editable that way) is not recommended for security reasons.
* Finished synchronization workflows cannot be inspected (unlike publication and localization workflows). 
* Synchronized contents will always show user _workflow_ as last editor.
