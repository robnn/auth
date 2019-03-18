package hu.robnn.auth.mock

import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.config.AutowireCapableBeanFactory
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationEvent
import org.springframework.context.MessageSourceResolvable
import org.springframework.core.ResolvableType
import org.springframework.core.env.Environment
import org.springframework.core.io.Resource
import java.util.*

class ApplicationContextMock: ApplicationContext {
    override fun getMessage(code: String, args: Array<Any>?, defaultMessage: String?, locale: Locale): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMessage(code: String, args: Array<Any>?, locale: Locale): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getMessage(resolvable: MessageSourceResolvable, locale: Locale): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getResource(location: String): Resource {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getId(): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getClassLoader(): ClassLoader? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBeanNamesForType(p0: ResolvableType): Array<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBeanNamesForType(p0: Class<*>?): Array<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBeanNamesForType(p0: Class<*>?, p1: Boolean, p2: Boolean): Array<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBeanNamesForAnnotation(p0: Class<out Annotation>): Array<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun containsLocalBean(p0: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBeanDefinitionCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAutowireCapableBeanFactory(): AutowireCapableBeanFactory {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBeansWithAnnotation(p0: Class<out Annotation>): MutableMap<String, Any> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getParentBeanFactory(): BeanFactory? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getParent(): ApplicationContext? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    override fun getBeanDefinitionNames(): Array<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T : Any?> getBeansOfType(p0: Class<T>?): MutableMap<String, T> {
        return mutableMapOf()
    }

    override fun <T : Any?> getBeansOfType(p0: Class<T>?, p1: Boolean, p2: Boolean): MutableMap<String, T> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toString(): String {
        return super.toString()
    }

    override fun getBean(p0: String): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T : Any?> getBean(p0: String, p1: Class<T>?): T {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getBean(p0: String, vararg p1: Any?): Any {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T : Any?> getBean(p0: Class<T>): T {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <T : Any?> getBean(p0: Class<T>, vararg p1: Any?): T {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isPrototype(p0: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getType(p0: String): Class<*>? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getDisplayName(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isTypeMatch(p0: String, p1: ResolvableType): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isTypeMatch(p0: String, p1: Class<*>?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun containsBeanDefinition(p0: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun publishEvent(event: ApplicationEvent) {
        super.publishEvent(event)
    }

    override fun publishEvent(event: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isSingleton(p0: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getStartupDate(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun containsBean(p0: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getEnvironment(): Environment {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getApplicationName(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getResources(locationPattern: String): Array<Resource> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun <A : Annotation?> findAnnotationOnBean(p0: String, p1: Class<A>): A? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAliases(p0: String): Array<String> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}