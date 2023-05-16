/*
 * MasterForm.java
 *
 * Created on December 02, 2005, 12:15 PM
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 *
 * 01/29/2006 Finally figured out how to handle nested
 * ActionForm and ArrayList.  Single ActionForm validation has
 * been going on since 12/2005.
 * 01/31/2006 Enhancement to the "mappedType" module to dynamically create
 * a "Form" or "DO" class depending on class and mode passed to the module.
 */

package com.wolmerica.tools.formatter;

/**
 *
 * @author Richard
 */
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

/**
 * An abstract base class for ActionForms that adds support for
 * automatic formatting and unformatting of string values and for the
 * transfer of the resulting values between itself and the given bean.
 * The <code>populate()</code> method provides an entry point to
 * this functionality, while the <code>keysToSkip()</code> method allows
 * subclasses to specify fields that should not be populated.
 * <p>
 * Additional methods are provided to allow subclasses to override
 * formatting defaults. The <code>setDefaultString()</code> method
 * allows callers to specify overrides for the default string used
 * to represent a <code>null</code> for a given property. Similarly, the
 * <code>setFormatterType()</code> method allows callers to specify a
 * formatting type other than the default for a given property.
 * <p>
 * Developers can also specify validation rules by invoking
 * <code>addRange()</code> to add a range validation or
 * <code>addRequiredFields() to add required field validations for an
 * array of property names.
 */
public abstract class MasterForm extends ActionForm {
   /** Global errors are needed to return nested errors */
    public ActionErrors globalErrors;
    /** Indicates that conversion is from string to object */
    public final static int TO_OBJECT = 0;
    /** Indicates that conversion is from object to string */
    public final static int TO_STRING = 1;
    /** Indicates that the object isn't part of a list */
    public final static int NO_LIST = -1;
    /** Formatter class to use for given property. Overrides default. */
    private Map formatMap = new HashMap();
    /** The value object associated with this form */
    private transient Object bean;
    /** The validation rules for this form */
    private Map validationMap = new HashMap();
    /**
     * The strings to display when null values are encountered.
     * Keys correspond to fields in the Form. The presence of a given
     * key indicates that the value provided in the map should be used
     * instead of the normal default string.
     */
    private Map defaultStringMap = new HashMap();

    /**
     * Populates the bean associated with this form and returns an
     * ActionErrors populated with validation errors, if any.
     * @return Validation errors.
     */

    @Override
    public ActionErrors validate(ActionMapping mapping,
                                 HttpServletRequest request) {
        globalErrors = new ActionErrors();
        ActionErrors errors = populate(this.bean, TO_OBJECT, NO_LIST);
        globalErrors.add(errors);
// System.out.println(" validate complete "+ globalErrors.size());

        return globalErrors;
    }

    /**
     * Populates the form with values from the given bean,
     * converting Java types to formatted string values suitable
     * for presentation in the user interface. Conversions are
     * applied automatically by introspecting the type of each
     * property and finding the appropriate Formatter class to use
     * based on the type information.
     * <p>
     * This behavior can be customized by calling
     * <code>setFormatterType(String key, Class type)</code>
     * with an alternative Formatter class for a given property name.
     * <p>
     * If null values are encountered in the bean, MasterForm will
     * be populated with the default string supplied by the
     * Formatter, unless the default has been overridden by calling
     * to <code>setDefaultString(String key, String value)</code> with
     * an alternative string.
     *
     * @param bean The bean to populate
     * @return Validation errors
     */
    public ActionErrors populate(Object bean) {
        globalErrors = new ActionErrors();
        return populate(bean, TO_STRING, NO_LIST);
    }

    /**
     * Adds an entry for the provided rule to the validation map.
     * @param key The name of the property to which the rule applies
     * @param rule The name of the rule
     * @param value A value or values used in executing the rule
     */
    public void addValidationRule(String key, String rule, Object value) {
        Map values = (Map) validationMap.get(key);
        if (values == null) {
            values = new HashMap();
            validationMap.put(key, values);
        }
        values.put(rule, value);
    }

    /**
     * Associates the "required field" rule with the provided keys
     * @param keys The names of the required fields
     */
    public void addRequiredFields(String[] keys) {
        for (int i = 0; i < keys.length; i++) {
            addValidationRule(keys[i], "required", Boolean.TRUE);
        }
    }

    /**
     * Adds a range rule for the provided key
     * @param key The name of the property to which the rule applies
     * @param min The minimum allowable value
     * @param max The maximum allowable value
     */
    public void addRange(String key, Comparable min, Comparable max) {
        Range range = new Range(min, max);
        addValidationRule(key, "range", range);
    }

    /**
     * Sets the default value to display for the given key when the
     * property value in the associated bean is <code>null</code>.
     *
     * @param key the name of the property
     * @param value the value to display
     */
    public void setDefaultString(String key, String value) {
        defaultStringMap.put(key, value);
    }

    /**
     * Sets the default Formatter class to use for the given key
     *
     * @param key the name of the property
     * @param value the value to display
     */
    public void setFormatterType(String key, Class type) {
        if (!Formatter.class.isAssignableFrom(type))
            throw new FormattingException(type + "must be a Formatter");
        formatMap.put(key, type);
    }

    public Object getBean() { return bean; }
    public void setBean(Object bean) { this.bean = bean; }

    /**
     * Transfers values to and from the given bean, depending on the
     * value of <code>mode</code>. If the given mode is
     * <code>TO_STRING</code>, populates
     * the instance by introspecting the specified bean,
     * converting any typed values to formatted strings, and
     * then using reflection to invoke its own String-based setter
     * methods. If the mode is <code>TO_OBJECT</code>, performs the
     * inverse operation, unformatting and converting properties of
     * the MasterFrom instance and populating the resulting values in the
     * given bean.
     * <p>
     * If null values are encountered in the bean, MasterFrom will
     * be populated with the default string associated with the given
     * type. The default null values can be overridden by calling
     * <code>setDefaultString(String key, String value)</code> with
     * an alternative string. The default Formatter to use can be
     * overridden by calling
     * <code>setFormatterType(String key, Class type)</code> with an
     * alternative Formatter class.
     * <p>
     * You ordinarily don't call this method directly; it is called
     * automatically by <code>validate()</code>.
     *
     * @param bean An object containing the values to be populated
     * @param mode Whether conversion is to String or to Java type
     * @return Validation errors
     */
    private ActionErrors populate(Object bean, int mode, Integer idx) {
        String errorMsg = "Unable to format values from bean: " + bean;
        Object target = (mode == TO_STRING ? bean : this);
        ActionErrors errors = new ActionErrors();
        Map valueMap = mapRepresentation(target, mode);

        if (mode == TO_STRING)
            setBean(bean);

        Iterator keyIter = valueMap.keySet().iterator();
        while (keyIter.hasNext()) {
            String currKey = (String)keyIter.next();
            Object currValue = valueMap.get(currKey);
            try {
// System.out.println(" populateProperty(..) " + currKey + "|" + currValue + "|" + mode+"|");
                ActionErrors currErrors = populateProperty(bean, currKey, currValue,
                                                           mode, idx);
                errors.add(currErrors);
            }
            catch (InstantiationException ie) {
                throw new FormattingException(errorMsg, ie);
            }
            catch (IllegalAccessException iae) {
                throw new FormattingException(errorMsg, iae);
            }
            catch (InvocationTargetException ite) {
                throw new FormattingException(errorMsg, ite);
            }
            catch (NoSuchMethodException nsme) {
                throw new FormattingException(errorMsg, nsme);
            }
        }
        return errors;
    }

    /**
     * Populates the property identified by the provided key.
     * Handling is provided for nested properties and Lists of
     * nested properties.
     *
     * @param bean The Java bean that contains the property
     * @param key The name of the property
     * @param obj The new value for the property
     * @param mode Whether to populate the bean or the form
     */
    private ActionErrors populateProperty(Object bean,
                                          String key,
                                          Object obj,
                                          int mode,
                                          Integer idx)
      throws InstantiationException, IllegalAccessException,
             NoSuchMethodException, InvocationTargetException {
        Object target = (mode == TO_STRING ? this : bean);
        Class type = PropertyUtils.getPropertyType(bean, key);
        ActionErrors errors = new ActionErrors();
        Object value = null;
        // Add a message key variable to avoid collision with key.
        String msgKey = key;
        // Over-ride the errorKey to include idx value for errors in list.
        if ( !(idx == NO_LIST))
          msgKey = key + idx.toString();
        boolean isNestedObject = isNestedObject(type);
        boolean isListOfNestedObjects = isListOfNestedObjects(obj, type);

        if (isNestedObject) {
          value = populateNestedObject(obj, mode, idx);
        }
        if (isListOfNestedObjects) {
            value = populateList((List) obj, mode);
        }
        if (!(isNestedObject || isListOfNestedObjects)) {
          if (mode == TO_STRING) {
            value = convert(type, key, obj, mode);
          }
          else {
            try {
              String errorKey = null;
              value = convert(type, key, obj, mode);

              if (!validateRequired(key,(String)obj)) {
                errorKey = "errors.required";
              }
              else if (!validateRange(key, value)) {
                errorKey = "errors.range";
              }
              if (errorKey != null) {
                errors.add(msgKey, new ActionMessage(errorKey));
              }
// System.out.println(" populateProperty key="+key+" errorKey="+ errorKey);
            }
            catch (FormattingException e) {                
              String errorKey = e.getFormatter().getErrorKey();
// System.out.println(" format exception="+e.getFormatter().getErrorKey());
// System.out.println(" populateProperty key="+key+" errors="+ errors.size());
              errors.add(msgKey, new ActionMessage(errorKey));
            }
          }
        }

        PropertyUtils.setSimpleProperty(target, key, value);
//--------------------------------------------------------------------------------
// The migration to MySQL has caused some problems with validation on Timestamp
// Even without validation turned on the transformation is causing errors.
// Here is a quick fix to clear out any *Stamp errors.  04/03/2006 4:06 PM
//--------------------------------------------------------------------------------
        return errors;
    }

    /**
     * Converts the provided object either from a String to a Java
     * type or vice versa, depending on the value of
     * <code>mode</code>.
     *
     * @param type The class of the associated bean property
     * @param key The name of the associated bean property
     * @param obj The object to convert
     * @param mode Whether conversion is to String or to Java type
     * @return The converted object
     */
    private Object convert(Class type,
                           String key,
                           Object obj,
                           int mode)
      throws InstantiationException, IllegalAccessException,
             NoSuchMethodException, InvocationTargetException {
        Object convertedObj = null;
// System.out.println(" convert(...) " + key + " " + type.getClass().getName());
        Formatter formatter = getFormatter(key, type);
// System.out.println(" getFormatter() " + formatter.getClass().getName());
// System.out.println(" pre convert() " + mode);
        try {
          switch (mode) {
            case TO_OBJECT:
              convertedObj = formatter.unformat((String)obj);
              break;
            case TO_STRING:
              if (obj == null)
                convertedObj = (String) defaultStringMap.get(key);
              else
                convertedObj = formatter.format(obj);
              break;
            default:
              throw new RuntimeException("Unknown mode: " + mode);
          }
// System.out.println(" post convert() " + mode);
        }
        catch (FormattingException e) {
          e.setFormatter(formatter);
          throw e;
        }
        return convertedObj;
    }

    /**
     * Returns a class corresponding to the provided class. Used by
     * the infrastructure to determine the binding between
     * ActionForms and their associated JavaBean classes. This method
     * must be overridden by subclasses that contain nested objects
     * to return the appropriate types.
     *
     * @param type The given class
     * @return The Class corresponding to the provided type
     */
    public Class mappedType(Class type, int mode) {
        String curClassPath = type.getName();
        String curClassSuffix = "";
        String newClassPath = "";
        String newClassSuffix = "";
        // This assumes that all classes will end with either
        // "Form" to represent the string version.
        // "DO" to represent the data object version.
        // We replace the suffix of the current class path depending
        // on the mode of the mapping.
        switch (mode) {
            case TO_OBJECT:
                curClassSuffix = "Form";
                newClassSuffix = "DO";
                break;
            case TO_STRING:
                curClassSuffix = "DO";
                newClassSuffix = "Form";
                break;
            default:
                throw new RuntimeException("Unknown mode: " + mode);
        }

        // get the index of new_version in program_line (-1 if no match)
        int startPos = curClassPath.indexOf( curClassSuffix );

        if ( startPos > 0)
        {
            // create the new string
            newClassPath = curClassPath.substring ( 0, startPos ) + newClassSuffix ;
        }
        else {
            throw new RuntimeException("Failed mapping for class: " + curClassPath);
        }
// System.out.println("newClassPath = " + newClassPath);

        // Pass the new class back to the caller.
        Class targetClass = null;
        try {
        targetClass = Class.forName(newClassPath);
// System.out.println(" Object type=" + targetClass.getName() );
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return targetClass;
    }

    /**
     * Returns <code>true</code> if the provided object is a List
     * containing nested objects; false otherwise.
     *
     * @param object The potential List
     * @param type The class of the provided object
     * @return boolean
     */
    public static boolean isListOfNestedObjects(Object object, Class type) {
        if (type == null)
            return false;
        if (List.class.isAssignableFrom(type)) {
            Iterator listIter = ((List) object).iterator();
            if (listIter.hasNext()) {
                Object obj = listIter.next();
                if (isNestedObject(obj.getClass()))
                    return true;
            }
        }
        return false;
    }

    /**
     * Returns <code>true</code> if an object of the provided type
     * should be considered a nested object, false otherwise.
     *
     * @param type The type to be tested
     * @return boolean
     */
    public static boolean isNestedObject(Class type) {
        if (type == null) {
            return false;
        }
        if (AbstractDO.class.isAssignableFrom(type) || MasterForm.class.isAssignableFrom(type)) {
            return true;
        }
        return false;
    }

    /**
     * If the provided List is non-null, returns a List of objects
     * populated with the values of the objects in the provided List.
     *
     * @param source The original list of objects
     * @param mode The direction in which binding is taking place
     * @return A List of objects
     */
    private List populateList(List source, int mode)
      throws InstantiationException, IllegalAccessException {
        if (source == null)
            return null;
        ArrayList target = new ArrayList();
        Iterator sourceIter = source.iterator();
        Integer idx = -1;
        while (sourceIter.hasNext()) {
            Object currObj = sourceIter.next();
            if (currObj instanceof AbstractDO ||
                currObj instanceof MasterForm) {
                Object newObj = populateNestedObject(currObj, mode, ++idx);
                target.add(newObj);
            }
        }
        return target;
    }

    /**
     * If the provided object is non-null, returns a new object
     * populated with values from the provided object. The new object
     * will be of the type returned by a call to the
     * <code>mappedType()</code> method, which subclasses should
     * override to return an appropriate type.
     *
     * @param source The nested object
     * @param mode The direction in which binding is taking place
     * @return An object populated with values from the nested
     * object.
     */
    private Object populateNestedObject(Object source, int mode, Integer idx)
      throws InstantiationException, IllegalAccessException {

        if (source == null)
            return null;
        Class sourceClass = source.getClass();
        Class targetClass = mappedType(sourceClass, mode);

        if (targetClass == null) {
            throw new FormattingException("No target class defined for " + sourceClass);
        }
        Object target = targetClass.newInstance();
        if (target instanceof MasterForm) {
            ActionErrors currErrors = ((MasterForm) target).populate(source , mode, idx);
            globalErrors.add(currErrors);
        }
        else {
            ActionErrors currErrors = ((MasterForm) source).populate(target, mode, idx);
            globalErrors.add(currErrors);
        }
        return target;
    }

    /**
     * Returns <code>false</code> if there is a rule corresponding to
     * the provided key and the given value does not fall within it;
     * <code>true</code> otherwise.
     * @param key The key under which the rule is stored
     * @param value The value to be validated
     * @return The result of the validation
     */
    private boolean validateRange(String key, Object value) {
        Map rules = (Map) validationMap.get(key);
        if (rules == null)
            return true;
        Range range = (Range) rules.get("range");
        return range == null || range.isInRange((Comparable) value);
    }

    /**
     * Returns <code>false</code> if the provided value is a required
     * field and is blank or null; <code>true</code> otherwise.
     * @param key The name of the field to be validated
     * @param value The value to be validated
     * @return The result of the validation
     */
    private boolean validateRequired(String key, String value) {
        Map rules = (Map) validationMap.get(key);

        if (rules == null) {
            return true;
        }
        Boolean required = (Boolean) rules.get("required");
        if (required == null) {
            return true;
        }
        boolean isRequired = required.booleanValue();
        boolean isBlank = (value == null || value.trim().equals(""));

        return !(isRequired && isBlank);
    }

    /**
     * Returns a Map containing the values from the provided
     * Java bean, keyed by field name. Entries having keys
     * that match any of the strings returned by
     * <code>keysToSkip()</code> will be removed.
     *
     * @param bean the Java bean from which to create the Map
     * @return a Map containing values from the provided bean
     */
    private Map mapRepresentation(Object bean, int mode) {
        String errorMsg = "Unable to format values from bean: " + bean;
        Map valueMap = null;

        // PropertyUtils.describe() uses Introspection to generate a Map
        // of values from its argument, keyed by field name.
        try {
            valueMap = PropertyUtils.describe(bean);
        }
        catch (IllegalAccessException iae) {
            throw new FormattingException(errorMsg, iae);
        }
        catch (InvocationTargetException ite) {
            throw new FormattingException(errorMsg, ite);
        }
        catch (NoSuchMethodException nsme) {
            throw new FormattingException(errorMsg, nsme);
        }

        // Remove keys for properties that shouldn't be populated.
        Iterator keyIter = keysToSkip(mode).iterator();
        while (keyIter.hasNext()) {
            String key = (String)keyIter.next();
            valueMap.remove(key);
        }

        return valueMap;
    }

    /**
     * Returns an array of keys, representing values that should not be
     * populated for the current form instance. Subclasses that override
     * this method to provide additional keys to be skipped should be
     * sure to call <code>super</code>
     *
     * @return an array of keys to be skipped
     */
    private ArrayList keysToSkip(int mode) {
        ArrayList keysToSkip = new ArrayList();
        keysToSkip.add("class");
        keysToSkip.add("servletWrapper");
        keysToSkip.add("multipartRequestHandler");
        keysToSkip.add("bean");
        return keysToSkip;
    }

    /**
     * Returns a Formatter for the provided type. If the provided key
     * matches an entry in the formatMap, the Formatter type indicated
     * by the entry is used instead of the default for the given type.
     *
     * @param key The name of the property to be formatted
     * @param type The type of the property to be formatted
     * @return A Formatter
     */
    private Formatter getFormatter(String key, Class type) {
      Class formatType = (Class) formatMap.get(key);
      if (formatType == null)
        return Formatter.getFormatter(type);
//------------------------------------------------------------------------------
// PercentageFormatter
//------------------------------------------------------------------------------
      if (formatType.isAssignableFrom(PercentageFormatter.class)) {
        if (!BigDecimal.class.isAssignableFrom(type))
          throw new FormattingException("Unable to format value "
            + "of type " + type + " as a percentage.");
        return new PercentageFormatter();
      }
//------------------------------------------------------------------------------
// PhoneNumberFormatter
//------------------------------------------------------------------------------
      if (formatType.isAssignableFrom(PhoneNumberFormatter.class)) {
        return new PhoneNumberFormatter();
      }
//------------------------------------------------------------------------------
// EmailFormatter
//------------------------------------------------------------------------------
      if (formatType.isAssignableFrom(EmailFormatter.class)) {
        return new EmailFormatter();
      }

      return null;
    }
}
